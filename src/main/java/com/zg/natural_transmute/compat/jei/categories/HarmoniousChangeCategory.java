package com.zg.natural_transmute.compat.jei.categories;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.blocks.entity.HarmoniousChangeStoveBlockEntity;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.registry.NTBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        NonNullList<ItemStack> resultItemList = recipe.getResultItemList();
        Set<Item> items = HarmoniousChangeStoveBlockEntity.getFuel().keySet();
        List<ItemStack> fuelList = new ArrayList<>(items.stream().map(ItemStack::new).toList());
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 12).addIngredients(recipe.input1);
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 12).addIngredients(recipe.input2);
        builder.addSlot(RecipeIngredientRole.INPUT, 41, 12).addIngredients(recipe.input3);
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 42).addItemStacks(fuelList);
        builder.addSlot(RecipeIngredientRole.INPUT, 65, 19).addIngredients(recipe.fuXiang);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 38).addItemStack(resultItemList.getFirst());
        if (resultItemList.size() == 2) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 38).addItemStack(resultItemList.get(1));
        }

        if (resultItemList.size() == 3) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 38).addItemStack(resultItemList.get(2));
        }
    }

}