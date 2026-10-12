package com.zg.natural_transmute.common.items.crafting;

import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.registry.NTRecipeSerializers;
import com.zg.natural_transmute.registry.NTRecipes;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A harmonious change recipe: three ordered input slots, one biome catalyst slot and three ordered output slots.
 * <p>
 * Every slot is positional. An input slot without an ingredient requires the stove slot to be empty,
 * and an output slot with {@link ItemStack#EMPTY} produces nothing. The biome catalyst is never consumed,
 * inputs are only consumed when {@link #isConsume()} is true.
 */
@Getter
public class HarmoniousChangeRecipe implements Recipe<HarmoniousChangeRecipeInput> {

    public static final int INPUT_SLOTS = 3;
    public static final int OUTPUT_SLOTS = 3;

    private final List<Optional<SizedIngredient>> inputs;
    private final Ingredient biomeCatalyst;
    private final List<ItemStack> outputs;
    private final boolean consume;

    public HarmoniousChangeRecipe(
            List<Optional<SizedIngredient>> inputs, Ingredient biomeCatalyst,
            List<ItemStack> outputs, boolean consume) {
        if (inputs.size() != INPUT_SLOTS || inputs.stream().noneMatch(Optional::isPresent)) {
            throw new IllegalArgumentException("Harmonious change recipe needs " + INPUT_SLOTS + " input slots with at least one input");
        } else if (outputs.size() != OUTPUT_SLOTS || outputs.stream().allMatch(ItemStack::isEmpty)) {
            throw new IllegalArgumentException("Harmonious change recipe needs " + OUTPUT_SLOTS + " output slots with at least one output");
        } else if (biomeCatalyst.isEmpty()) {
            throw new IllegalArgumentException("Harmonious change recipe needs a biome catalyst");
        }

        this.inputs = List.copyOf(inputs);
        this.biomeCatalyst = biomeCatalyst;
        this.outputs = outputs.stream().map(ItemStack::copy).toList();
        this.consume = consume;
    }

    @Override
    public boolean matches(HarmoniousChangeRecipeInput input, Level level) {
        if (!this.biomeCatalyst.test(input.biomeCatalyst())) {
            return false;
        }

        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = input.getItem(i);
            Optional<SizedIngredient> ingredient = this.inputs.get(i);
            if (ingredient.isPresent() ? !ingredient.get().test(stack) : !stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return fresh copies of the stacks to put into the three output slots, {@link ItemStack#EMPTY} for unused slots.
     */
    public List<ItemStack> assembleOutputs(HarmoniousChangeRecipeInput input, HolderLookup.Provider registries) {
        List<ItemStack> results = new ArrayList<>(OUTPUT_SLOTS);
        for (ItemStack output : this.outputs) {
            results.add(output.copy());
        }

        return results;
    }

    @Override
    public ItemStack assemble(HarmoniousChangeRecipeInput input, HolderLookup.Provider registries) {
        return this.assembleOutputs(input, registries).stream()
                .filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.outputs.stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * Only the ingredients of the defined input slots, use {@link #getInputs()} for positional access.
     */
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        this.inputs.forEach(input -> input.ifPresent(ingredient -> ingredients.add(ingredient.ingredient())));
        return ingredients;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(NTBlocks.HARMONIOUS_CHANGE_STOVE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeSerializers.HARMONIOUS_CHANGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NTRecipes.HARMONIOUS_CHANGE_RECIPE.get();
    }

}
