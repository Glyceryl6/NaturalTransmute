package com.zg.natural_transmute.common.data.recipes;

import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeSerializer;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
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

import java.util.*;
import java.util.function.Predicate;

/**
 * {@code requires} and {@code results} fill the lowest undefined slot, {@code input} and {@code output}
 * set a specific slot (0 to 2) so that slots in between can be left empty.
 */
public class HarmoniousChangeRecipeBuilder implements RecipeBuilder {

    private final List<Optional<SizedIngredient>> inputs = new ArrayList<>(Collections.nCopies(HarmoniousChangeRecipe.INPUT_SLOTS, Optional.empty()));
    private final List<ItemStack> outputs = new ArrayList<>(Collections.nCopies(HarmoniousChangeRecipe.OUTPUT_SLOTS, ItemStack.EMPTY));
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Getter
    private final Ingredient biomeCatalyst;
    private HarmoniousChangeSerializer.Factory<?> factory = HarmoniousChangeRecipe::new;
    private int time = HarmoniousChangeRecipe.DEFAULT_TIME;
    private boolean consume = true;
    @Getter
    private String name = StringUtils.EMPTY;

    private HarmoniousChangeRecipeBuilder(Ingredient biomeCatalyst) {
        if (biomeCatalyst.isEmpty()) {
            throw new IllegalArgumentException("Biome catalyst of a harmonious change recipe must not be empty");
        }

        this.biomeCatalyst = biomeCatalyst;
    }

    public static HarmoniousChangeRecipeBuilder addRecipe(ItemLike... biomeCatalyst) {
        return addRecipe(Ingredient.of(biomeCatalyst));
    }

    public static HarmoniousChangeRecipeBuilder addRecipe(Ingredient biomeCatalyst) {
        return new HarmoniousChangeRecipeBuilder(biomeCatalyst);
    }

    public HarmoniousChangeRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public HarmoniousChangeRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    /**
     * @param quantity the number of input slots to fill, each one requires a single item.
     */
    public HarmoniousChangeRecipeBuilder requires(ItemLike item, int quantity) {
        return this.requires(Ingredient.of(item), quantity);
    }

    public HarmoniousChangeRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    /**
     * @param quantity the number of input slots to fill, each one requires a single item.
     */
    public HarmoniousChangeRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.requires(new SizedIngredient(ingredient, 1));
        }

        return this;
    }

    public HarmoniousChangeRecipeBuilder requires(SizedIngredient ingredient) {
        return this.input(firstEmptySlot(this.inputs, Optional::isEmpty), ingredient);
    }

    public HarmoniousChangeRecipeBuilder input(int slot, ItemLike item) {
        return this.input(slot, Ingredient.of(item));
    }

    public HarmoniousChangeRecipeBuilder input(int slot, Ingredient ingredient) {
        return this.input(slot, new SizedIngredient(ingredient, 1));
    }

    public HarmoniousChangeRecipeBuilder input(int slot, SizedIngredient ingredient) {
        if (ingredient.ingredient().isEmpty()) {
            throw new IllegalArgumentException("Input of a harmonious change recipe must not be empty");
        } else if (this.inputs.get(slot).isPresent()) {
            throw new IllegalStateException("Input slot " + slot + " is already defined");
        }

        this.inputs.set(slot, Optional.of(ingredient));
        return this;
    }

    public HarmoniousChangeRecipeBuilder results(ItemLike itemLike) {
        return this.results(itemLike, 1);
    }

    public HarmoniousChangeRecipeBuilder results(ItemLike itemLike, int count) {
        return this.results(new ItemStack(itemLike, count));
    }

    public HarmoniousChangeRecipeBuilder results(ItemStack itemStack) {
        return this.output(firstEmptySlot(this.outputs, ItemStack::isEmpty), itemStack);
    }

    public HarmoniousChangeRecipeBuilder output(int slot, ItemLike itemLike, int count) {
        return this.output(slot, new ItemStack(itemLike, count));
    }

    public HarmoniousChangeRecipeBuilder output(int slot, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            throw new IllegalArgumentException("Output of a harmonious change recipe must not be empty");
        } else if (!this.outputs.get(slot).isEmpty()) {
            throw new IllegalStateException("Output slot " + slot + " is already defined");
        }

        this.outputs.set(slot, itemStack.copy());
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

    /**
     * Saves the recipe as a subclass of {@link HarmoniousChangeRecipe}, e.g. a special recipe sharing the same data.
     */
    public HarmoniousChangeRecipeBuilder factory(HarmoniousChangeSerializer.Factory<?> factory) {
        this.factory = factory;
        return this;
    }

    public List<Optional<SizedIngredient>> getInputs() {
        return Collections.unmodifiableList(this.inputs);
    }

    public List<ItemStack> getOutputs() {
        return Collections.unmodifiableList(this.outputs);
    }

    @Override
    public HarmoniousChangeRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public HarmoniousChangeRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.outputs.stream().filter(stack -> !stack.isEmpty())
            .findFirst().orElse(ItemStack.EMPTY).getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ResourceLocation recipeId = id.withPrefix("harmonious_change/");
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        } else if (this.inputs.stream().noneMatch(Optional::isPresent) || this.outputs.stream().allMatch(ItemStack::isEmpty)) {
            throw new IllegalStateException("Recipe " + recipeId + " needs at least one input and one output");
        }

        Advancement.Builder builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(builder::addCriterion);
        HarmoniousChangeRecipe recipe = this.factory.create(this.inputs, this.biomeCatalyst, this.outputs, this.time, this.consume);
        recipeOutput.accept(recipeId, recipe, builder.build(id.withPrefix("recipes/harmonious_change/")));
    }

    private static <E> int firstEmptySlot(List<E> slots, Predicate<E> isEmpty) {
        for (int i = 0; i < slots.size(); i++) {
            if (isEmpty.test(slots.get(i))) {
                return i;
            }
        }

        throw new IllegalStateException("A harmonious change recipe has only " + slots.size() + " slots");
    }

}
