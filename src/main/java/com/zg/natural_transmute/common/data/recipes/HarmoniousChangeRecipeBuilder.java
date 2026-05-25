package com.zg.natural_transmute.common.data.recipes;

import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HarmoniousChangeRecipeBuilder implements RecipeBuilder {

    public final List<SizedIngredient> ingredients = NonNullList.create();
    public final List<Ingredient> excepts = NonNullList.create();
    public final List<ItemStack> results = NonNullList.create();
    public final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    public final Ingredient biomeCatalyst;
    public int time = 160;
    public boolean consume = true;
    public String name = StringUtils.EMPTY;

    private HarmoniousChangeRecipeBuilder(ItemLike... biomeCatalyst) {
        this.biomeCatalyst = Ingredient.of(biomeCatalyst);
    }

    public static HarmoniousChangeRecipeBuilder addRecipe(ItemLike... biomeCatalyst) {
        return new HarmoniousChangeRecipeBuilder(biomeCatalyst);
    }

    public HarmoniousChangeRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public HarmoniousChangeRecipeBuilder requires(ItemLike item, int quantity) {
        return this.requires(SizedIngredient.of(item, quantity));
    }

    public HarmoniousChangeRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(tag, 1);
    }

    public HarmoniousChangeRecipeBuilder requires(TagKey<Item> tag, int quantity) {
        return this.requires(SizedIngredient.of(tag, quantity));
    }

    public HarmoniousChangeRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public HarmoniousChangeRecipeBuilder requires(Ingredient ingredient, int quantity) {
        return this.requires(new SizedIngredient(ingredient, quantity));
    }

    public HarmoniousChangeRecipeBuilder requires(SizedIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public HarmoniousChangeRecipeBuilder excepts(ItemLike itemLike) {
        this.excepts.add(Ingredient.of(itemLike));
        return this;
    }

    public HarmoniousChangeRecipeBuilder results(ItemLike itemLike) {
        return this.results(itemLike, 1);
    }

    public HarmoniousChangeRecipeBuilder results(ItemLike itemLike, int count) {
        return this.results(new ItemStack(itemLike, count));
    }

    public HarmoniousChangeRecipeBuilder results(ItemStack itemStack) {
        this.results.add(itemStack);
        return this;
    }

    public HarmoniousChangeRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    public HarmoniousChangeRecipeBuilder consume(boolean consume) {
        this.consume = consume;
        return this;
    }

    public HarmoniousChangeRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.results.getFirst().getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        if (this.criteria.isEmpty()) throw new IllegalStateException("No way of obtaining recipe " + id);
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        HarmoniousChangeRecipe recipe = new HarmoniousChangeRecipe(this.ingredients, this.excepts, this.results, this.biomeCatalyst, this.time, this.consume);
        recipeOutput.accept(id.withPrefix("harmonious_change/"), recipe, builder.build(id.withPrefix("recipes/harmonious_change/")));
    }
}