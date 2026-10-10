package com.zg.natural_transmute.compat.jei.categories;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.utils.HarmoniousChangeFuelUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class HarmoniousChangeCategory implements IRecipeCategory<HarmoniousChangeRecipe> {

    public static final RecipeType<HarmoniousChangeRecipe> HARMONIOUS_CHANGE_STOVE_RECIPE =
        RecipeType.create(NaturalTransmute.MOD_ID, "harmonious_change", HarmoniousChangeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public HarmoniousChangeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = NaturalTransmute.prefix("textures/gui/harmonious_change_stove_jei.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 169, 68);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(NTBlocks.HARMONIOUS_CHANGE_STOVE.get()));
        this.localizedName = Component.translatable(NTBlocks.HARMONIOUS_CHANGE_STOVE.get().getDescriptionId());
    }

    @Override
    public RecipeType<HarmoniousChangeRecipe> getRecipeType() {
        return HARMONIOUS_CHANGE_STOVE_RECIPE;
    }

    @Override
    public Component getTitle() {
        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HarmoniousChangeRecipe recipe, IFocusGroup focuses) {
        List<Optional<SizedIngredient>> inputs = recipe.getInputs();
        for (int i = 0; i < HarmoniousChangeRecipe.INPUT_SLOTS; i++) {
            int x = 5 + 18 * i;
            inputs.get(i).ifPresent(ingredient ->
                builder.addSlot(RecipeIngredientRole.INPUT, x, 12)
                    .addItemStacks(List.of(ingredient.getItems()))
            );
        }

        List<ItemStack> fuelList = HarmoniousChangeFuelUtils.getFuelsItemView().stream().map(ItemStack::new).toList();
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 42).addItemStacks(fuelList);
        builder.addSlot(RecipeIngredientRole.INPUT, 65, 19).addIngredients(recipe.getBiomeCatalyst());
        List<ItemStack> outputs = recipe.getOutputs();
        for (int i = 0; i < HarmoniousChangeRecipe.OUTPUT_SLOTS; i++) {
            ItemStack output = outputs.get(i);
            if (!output.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 102 + 18 * i, 38).addItemStack(output);
            }
        }
    }

}