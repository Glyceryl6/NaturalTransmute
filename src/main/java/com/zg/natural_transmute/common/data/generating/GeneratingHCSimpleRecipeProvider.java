package com.zg.natural_transmute.common.data.generating;

import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.common.data.generating.format.IGeneratingTable;
import com.zg.natural_transmute.common.data.recipes.HarmoniousChangeRecipeBuilder;
import com.zg.natural_transmute.utils.IdUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.zg.natural_transmute.utils.IdUtils.modLoc;

public class GeneratingHCSimpleRecipeProvider extends GeneratingHCRecipeProvider {

    public GeneratingHCSimpleRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId, IGeneratingTable table, GeneratingItemTagsProvider tags) {
        super(output, registries, modId, table, tags);
    }

    @Override
    public String getName() {
        return modId + ": HCSimpleRecipe";
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        var bcKeys = new HashMap<Integer, Either<ResourceKey<Item>, TagKey<Item>>>();
        var bcIngredients = new HashMap<Integer, Ingredient>();
        {
            for (var column = 0; column < table.getColumns(); column++) {
                if (table.hasValue(0, column)) {
                    var cell = table.getValue(0, column);
                    if (cell.isBlank()) {
                        continue;
                    }
                    var key = idOrCreatedTag(cell);
                    bcKeys.put(column, key);
                    var ingredient = ingredientOf(key, registries);
                    bcIngredients.put(column, ingredient);
                }
            }
        }

        for (var row = 1; row < table.getRows(); row++) {
            var cellKeys = new HashMap<Integer, Either<ResourceKey<Item>, TagKey<Item>>>();
            for (var column = 0; column < table.getColumns(); column++) {
                if (table.hasValue(row, column)) {
                    var cell = table.getValue(row, column);
                    if (cell.isBlank()) {
                        continue;
                    }
                    var key = idOrCreatedTag(cell);
                    cellKeys.put(column, key);
                }
            }
            var keySet = new HashSet<>(cellKeys.values());
            var inputTag = tags.anonymousTag(keySet);
            var inputIngredient = Ingredient.of(inputTag);
            for (var column = 0; column < table.getColumns(); column++) {
                if (table.hasValue(row, column)) {
                    var cell = table.getValue(row, column);
                    if (cell.isBlank()) {
                        continue;
                    }
                    if (!cellKeys.containsKey(column)) {
                        continue;
                    }
                    if (!bcIngredients.containsKey(column)) {
                        continue;
                    }
                    var bcKey = bcKeys.get(column);
                    var bcIngredient = bcIngredients.get(column);
                    var outputId = table.getId(Registries.ITEM, row, column);
                    var outputHolder = holderOf(outputId, registries);
                    var outputItem = new ItemStack(outputHolder);
                    var name = uniqueHashName(bcKey, keySet);
                    var id = modLoc("simple_generated/unique_key_" + name);
                    HarmoniousChangeRecipeBuilder.addRecipe(bcIngredient)
                        .input(0, inputIngredient)
                        .output(0, outputItem)
                        .unlockedBy(IdUtils.normalized(id) + "_has_biome_catalyst", inventoryTrigger(itemPredicateBuilderOf(bcKey, registries)))
                        .unlockedBy("has_" + IdUtils.normalized(inputTag.location()), has(inputTag))
                        .save(output, id);
                }
            }
        }
    }

    private static int uniqueHashName(Either<ResourceKey<Item>, TagKey<Item>> bc,
                                      Set<Either<ResourceKey<Item>, TagKey<Item>>> inputs) {
        return Objects.hash(IdUtils.string(bc), GeneratingItemTagsProvider.getTagHashCode(inputs));
    }
}
