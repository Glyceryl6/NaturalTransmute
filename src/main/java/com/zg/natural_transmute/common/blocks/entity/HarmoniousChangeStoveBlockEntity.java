package com.zg.natural_transmute.common.blocks.entity;

import com.zg.natural_transmute.client.inventory.HarmoniousChangeStoveMenu;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipeInput;
import com.zg.natural_transmute.registry.NTBlockEntityTypes;
import com.zg.natural_transmute.registry.NTRecipes;
import com.zg.natural_transmute.utils.HarmoniousChangeFuelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class HarmoniousChangeStoveBlockEntity extends SimpleContainerBlockEntity {
    private static final int INPUT_A_SLOT = 0;
    private static final int INPUT_B_SLOT = 1;
    private static final int INPUT_C_SLOT = 2;
    private static final int FUEL_SLOT = 3;
    private static final int BIOME_CATALYST_SLOT = 4;
    private static final int OUTPUT_A_SLOT = 5;
    private static final int OUTPUT_B_SLOT = 6;
    private static final int OUTPUT_C_SLOT = 7;

    private static final int MAX_FUEL_DURATION = 8;    // Durin_Skeleton assumes max is always 8.

    public static final int IDLING_STATE = 0;
    public static final int WORKING_STATE = 1;

    private int time;
    private int totalTime;
    private int currentState;

    private int fuelRemain = 0;

    @Nullable
    public BlockPos mainPos;
    private final ContainerData containerData = new Data();
    private final RecipeManager.CachedCheck<HarmoniousChangeRecipeInput, ? extends HarmoniousChangeRecipe> quickCheck;

    public HarmoniousChangeStoveBlockEntity(BlockPos pos, BlockState blockState) {
        super(NTBlockEntityTypes.HARMONIOUS_CHANGE_STOVE.get(), pos, blockState);
        this.quickCheck = RecipeManager.createCheck(NTRecipes.HARMONIOUS_CHANGE_RECIPE.get());
        this.handler = new Handler(8);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HarmoniousChangeStoveBlockEntity blockEntity) {
        boolean shouldReset = false;
        boolean changed = false;

        changed |= blockEntity.tryConsumeFuel();

        if (blockEntity.hasInput()) {
            HarmoniousChangeRecipeInput input = blockEntity.getRecipeInput();
            HarmoniousChangeRecipe recipe = blockEntity.checkHarmoniousChangeRecipe(input);
            if (recipe != null) {
                List<ItemStack> results = recipe.assembleOutputs(input, level.registryAccess());
                if (blockEntity.canAcceptResult(results)) {
                    if (!blockEntity.hasFuelRemain()) {
                        shouldReset = true;
                    }

                    var isRecipeDone = blockEntity.processRecipe(recipe, results);
                    if (isRecipeDone) {
                        blockEntity.currentState = IDLING_STATE;
                        blockEntity.fuelRemain -= 1;
                    } else {
                        blockEntity.currentState = WORKING_STATE;
                    }
                    changed = true;
                } else {
                    shouldReset = true;
                }
            } else {
                shouldReset = true;
            }
        } else if (blockEntity.time > 0) {
            shouldReset = true;
        }

        if (shouldReset) {
            blockEntity.time = 0;
            // XXX: totalTime is not using here, fill it in processRecipe :(
            blockEntity.currentState = IDLING_STATE;
        }

        if (changed || shouldReset) {
            setChanged(level, pos, state);
        }
    }

    // region Fuel

    private boolean tryConsumeFuel() {
        if (level == null || level.isClientSide) {
            return false;
        }

        var fuel = getItem(FUEL_SLOT);
        if (HarmoniousChangeFuelUtils.isCoalFuel(fuel)) {
            if (fuelRemain <= 0) {
                fuel.shrink(1);
                fuelRemain = MAX_FUEL_DURATION;
                return true;
            }
        } else if (HarmoniousChangeFuelUtils.isBucketFuel(fuel)) {
            if (fuelRemain < MAX_FUEL_DURATION) {
                var fuelToConsume = MAX_FUEL_DURATION - fuelRemain;
                var bucketDurabilityRemain = fuel.getMaxDamage() - fuel.getDamageValue();
                var actualConsume = Math.min(fuelToConsume, bucketDurabilityRemain);
                fuel.hurtAndBreak(actualConsume, (ServerLevel) level, null, item -> {
                });
                if (fuel.isEmpty()) {
                    ItemStack newItem = fuel.transmuteCopyIgnoreEmpty(Items.BUCKET, 1);
                    if (newItem.isDamageableItem()) {
                        newItem.setDamageValue(0);
                    }
                    setItem(FUEL_SLOT, newItem);
                }
                fuelRemain += actualConsume;
                return true;
            }
        }

        return false;
    }

    // endregion


    /**
     * Tick process recipe.
     *
     * @param recipe  Recipe.
     * @param results Stacks assembled by the recipe for the three output slots.
     * @return true for the recipe process was done, false for still processing.
     */
    private boolean processRecipe(HarmoniousChangeRecipe recipe, List<ItemStack> results) {
        ++this.time;
        this.totalTime = recipe.getTime();
        if (this.time < this.totalTime) {
            return false;
        }

        this.time = 0;
        for (int i = 0; i < HarmoniousChangeRecipe.OUTPUT_SLOTS; i++) {
            ItemStack resultStack = results.get(i);
            if (resultStack.isEmpty()) {
                continue;
            }

            ItemStack outStack = this.handler.getStackInSlot(OUTPUT_A_SLOT + i);
            if (outStack.isEmpty()) {
                this.handler.setStackInSlot(OUTPUT_A_SLOT + i, resultStack);
            } else {
                outStack.grow(resultStack.getCount());
            }
        }

        if (recipe.isConsume()) {
            this.consumeInputs(recipe);
        }

        return true;
    }

    private void consumeInputs(HarmoniousChangeRecipe recipe) {
        for (int i = 0; i < HarmoniousChangeRecipe.INPUT_SLOTS; i++) {
            int slot = INPUT_A_SLOT + i;
            recipe.getInputs().get(i).ifPresent(ingredient -> {
                ItemStack stack = this.getItem(slot);
                ItemStack remainingItem = stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY;
                stack.shrink(ingredient.count());
                if (!remainingItem.isEmpty()) {
                    remainingItem = remainingItem.copyWithCount(remainingItem.getCount() * ingredient.count());
                    if (stack.isEmpty()) {
                        this.handler.setStackInSlot(slot, remainingItem);
                    } else if (this.level != null) {
                        float x = this.worldPosition.getX() + 0.5F;
                        float y = this.worldPosition.getY() + 1.2F;
                        float z = this.worldPosition.getZ() + 0.5F;
                        Containers.dropItemStack(this.level, x, y, z, remainingItem);
                    }
                }
            });
        }
    }

    @Nullable
    private HarmoniousChangeRecipe checkHarmoniousChangeRecipe(HarmoniousChangeRecipeInput input) {
        if (this.level == null) {
            return null;
        }

        if (input.biomeCatalyst().isEmpty() || input.inputs().stream().allMatch(ItemStack::isEmpty)) {
            return null;
        }

        RecipeHolder<? extends HarmoniousChangeRecipe> holder = this.quickCheck.getRecipeFor(input, this.level).orElse(null);
        return holder != null ? holder.value() : null;
    }

    private HarmoniousChangeRecipeInput getRecipeInput() {
        List<ItemStack> inputs = List.of(this.getItem(INPUT_A_SLOT), this.getItem(INPUT_B_SLOT), this.getItem(INPUT_C_SLOT));
        return new HarmoniousChangeRecipeInput(inputs, this.getItem(BIOME_CATALYST_SLOT));
    }

    private boolean hasFuelRemain() {
        return fuelRemain > 0;
    }

    private boolean hasInput() {
        for (int i = INPUT_A_SLOT; i <= INPUT_C_SLOT; ++i) {
            if (!this.handler.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Will all results be put into output slots?
     *
     * @param results Stacks assembled by the recipe, each one only goes into its own output slot.
     */
    private boolean canAcceptResult(List<ItemStack> results) {
        for (int i = 0; i < HarmoniousChangeRecipe.OUTPUT_SLOTS; i++) {
            ItemStack resultStack = results.get(i);
            if (resultStack.isEmpty()) {
                continue;
            }

            int slot = OUTPUT_A_SLOT + i;
            ItemStack outStack = this.handler.getStackInSlot(slot);
            if (!outStack.isEmpty()
                &&
                (
                    !ItemStack.isSameItemSameComponents(outStack, resultStack)
                        ||
                        (
                            (outStack.getCount() + resultStack.getCount())
                                > Math.min(this.handler.getSlotLimit(slot), resultStack.getMaxStackSize())
                        )
                )
            ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.time = tag.getInt("Time");
        this.totalTime = tag.getInt("TotalTime");
        this.fuelRemain = tag.getInt("FuelRemain");
        this.currentState = tag.getInt("CurrentState");
        this.mainPos = NbtUtils.readBlockPos(tag, "MainPos").orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Time", this.time);
        tag.putInt("TotalTime", this.totalTime);
        tag.putInt("FuelRemain", this.fuelRemain);
        tag.putInt("CurrentState", this.currentState);
        if (this.mainPos != null) {
            tag.put("MainPos", NbtUtils.writeBlockPos(this.mainPos));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        ContainerLevelAccess access = ContainerLevelAccess.create(Objects.requireNonNull(this.level), this.worldPosition);
        return new HarmoniousChangeStoveMenu(containerId, inventory, access, this.handler, this.containerData);
    }

    public class Data implements ContainerData {
        public static final int TIME = 0;
        public static final int TOTAL_TIME = 1;
        public static final int CURRENT_STATE = 2;
        public static final int FUEL_REMAIN = 3;

        public static final int COUNT = 4;

        @Override
        public int get(int index) {
            if (index == TIME) {
                return time;
            } else if (index == TOTAL_TIME) {
                return totalTime;
            } else if (index == CURRENT_STATE) {
                return currentState;
            } else if (index == FUEL_REMAIN) {
                return fuelRemain;
            } else {
                return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            if (index == TIME) {
                time = value;
            } else if (index == TOTAL_TIME) {
                totalTime = value;
            } else if (index == CURRENT_STATE) {
                currentState = value;
            } else if (index == FUEL_REMAIN) {
                fuelRemain = Mth.clamp(value, 0, MAX_FUEL_DURATION);
            }
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }
}