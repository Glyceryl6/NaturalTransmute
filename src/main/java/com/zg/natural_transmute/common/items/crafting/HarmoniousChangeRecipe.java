package com.zg.natural_transmute.common.items.crafting;

import com.google.common.base.Preconditions;
import com.zg.natural_transmute.registry.NTDataComponents;
import com.zg.natural_transmute.registry.NTRecipeSerializers;
import com.zg.natural_transmute.registry.NTRecipes;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

@Getter
public class HarmoniousChangeRecipe implements Recipe<HarmoniousChangeRecipeInput> {
    private final List<SizedIngredient> ingredients;
    private final List<Ingredient> excepts;
    private final List<ItemStack> results;
    private final Ingredient biomeCatalyst;
    private final int time;
    private final boolean consume;

    public HarmoniousChangeRecipe(
            List<SizedIngredient> ingredients,
            List<Ingredient> excepts,
            List<ItemStack> results,
            Ingredient biomeCatalyst,
            int time, boolean consume) {
        Preconditions.checkArgument(ingredients.size() > 3, "Ingredients size must be less than 3.");
        Preconditions.checkArgument(results.size() > 3, "Results size must be less than 3.");
        this.ingredients = ingredients;
        this.excepts = excepts;
        this.results = results;
        this.biomeCatalyst = biomeCatalyst;
        this.time = time;
        this.consume = consume;
    }

    public HarmoniousChangeRecipe(List<SizedIngredient> ingredients, List<ItemStack> results, Ingredient biomeCatalyst) {
        this(ingredients, NonNullList.create(), results, biomeCatalyst, 160, Boolean.TRUE);
    }

    protected boolean extraMatches(HarmoniousChangeRecipeInput input) {
        return input.size() == 1 && this.getIngredients().size() == 1
                ? this.getIngredients().getFirst().test(input.getItem(0))
                : input.stackedContents().canCraft(this, (null));
    }

    @Override
    public boolean matches(HarmoniousChangeRecipeInput input, Level level) {
        if (input.ingredientCount() != this.getIngredients().size()) {
            return false;
        } else {
            Item item = input.getItem(HarmoniousChangeRecipeInput.BIOME_CATALYST_SLOT).getItem();
            boolean flag2 = this.getBiomeCatalysts().test(input.getItem(HarmoniousChangeRecipeInput.BIOME_CATALYST_SLOT));
            boolean flag3 = item.components().has(NTDataComponents.ASSOCIATED_BIOMES.get());
            return flag2 && flag3 && this.extraMatches(input);
        }
    }

    @Override
    public ItemStack assemble(HarmoniousChangeRecipeInput input, HolderLookup.Provider registries) {
        return this.getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > this.getIngredients().size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.getResults().getFirst();
    }

    public Ingredient getBiomeCatalysts() {
        return this.biomeCatalyst;
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