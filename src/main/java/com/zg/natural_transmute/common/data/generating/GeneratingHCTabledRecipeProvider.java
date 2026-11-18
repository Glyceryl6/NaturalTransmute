package com.zg.natural_transmute.common.data.generating;

import com.zg.natural_transmute.common.data.generating.format.IGeneratingTable;
import com.zg.natural_transmute.common.data.recipes.HarmoniousChangeRecipeBuilder;
import com.zg.natural_transmute.utils.IdUtils;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.concurrent.CompletableFuture;

public class GeneratingHCTabledRecipeProvider extends GeneratingHCRecipeProvider {
    private static final int COLUMN_COUNT = 21;

    private static final int RECIPE_NAME = 0;
    private static final int INPUT_1_ID = 1;
    private static final int INPUT_1_COUNT = 2;
    private static final int INPUT_1_COMPONENT = 3;
    private static final int INPUT_2_ID = 4;
    private static final int INPUT_2_COUNT = 5;
    private static final int INPUT_2_COMPONENT = 6;
    private static final int INPUT_3_ID = 7;
    private static final int INPUT_3_COUNT = 8;
    private static final int INPUT_3_COMPONENT = 9;
    private static final int BIOME_CATALYST = 10;
    private static final int OUTPUT_1_ID = 11;
    private static final int OUTPUT_1_COUNT = 12;
    private static final int OUTPUT_1_COMPONENT = 13;
    private static final int OUTPUT_2_ID = 14;
    private static final int OUTPUT_2_COUNT = 15;
    private static final int OUTPUT_2_COMPONENT = 16;
    private static final int OUTPUT_3_ID = 17;
    private static final int OUTPUT_3_COUNT = 18;
    private static final int OUTPUT_3_COMPONENT = 19;
    private static final int CONSUME_MATERIAL = 20;

    public GeneratingHCTabledRecipeProvider(PackOutput output,
                                            CompletableFuture<HolderLookup.Provider> registries,
                                            String modId,
                                            IGeneratingTable table,
                                            GeneratingItemTagsProvider tags) {
        super(output, registries, modId, table, tags);
    }

    @Override
    public String getName() {
        return modId + ": HCRecipeGenerator";
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        if (table.getColumns() != COLUMN_COUNT) {
            throw new IllegalArgumentException("Table size mismatch " + table.getRows() + "x" + table.getColumns());
        }

        for (int row = 0; row < table.getRows(); row++) {
            var name = table.getValue(row, RECIPE_NAME);
            var biomeCatalyst = idOrCreatedTag(table.getValue(row, BIOME_CATALYST));
            var biomeCatalystIngredient = ingredientOf(biomeCatalyst, registries);
            var biomeCatalystPredicate = itemPredicateBuilderOf(biomeCatalyst, registries);
            var builder = HarmoniousChangeRecipeBuilder.addRecipe(biomeCatalystIngredient);
            builder.unlockedBy(IdUtils.normalized(name) + "_has_biome_catalyst", inventoryTrigger(biomeCatalystPredicate));
            for (var i = 0; i < 3; i++) {
                var offset = i * 3;
                if (!table.hasValue(row, INPUT_1_ID + offset)) {
                    continue;
                }
                var id = idOrCreatedTag(table.getValue(row, INPUT_1_ID + offset));
                var count = table.getPositiveNumber(row, INPUT_1_COUNT + offset, 1);
                SizedIngredient sized;
                ItemPredicate.Builder predicateBuilder = itemPredicateBuilderOf(id, registries).withCount(MinMaxBounds.Ints.atLeast(count));
                if (table.hasValue(row, INPUT_1_COMPONENT + offset)) {
                    var component = table.getValue(row, INPUT_1_COMPONENT + offset);
                    var patch = componentPatchOf(component, registries);
                    var componentPredicate = componentPredicateOf(patch);
                    var holderSet = holderSetOf(id, registries);
                    var ingredient = DataComponentIngredient.of(false, componentPredicate, holderSet);
                    sized = new SizedIngredient(ingredient, count);
                    predicateBuilder.hasComponents(componentPredicate);
                } else {
                    var ingredient = ingredientOf(id, registries);
                    sized = new SizedIngredient(ingredient, count);
                }
                builder.input(i, sized);
                builder.unlockedBy(IdUtils.normalized(name) + "_has_input_" + i, inventoryTrigger(predicateBuilder));
            }
            for (var i = 0; i < 3; i++) {
                var offset = i * 3;
                if (!table.hasValue(row, OUTPUT_1_ID + offset)) {
                    continue;
                }

                var id = table.getId(Registries.ITEM, row, OUTPUT_1_ID + offset);
                var count = table.getPositiveNumber(row, OUTPUT_1_COUNT + offset, 1);
                var holder = holderOf(id, registries);
                DataComponentPatch component;
                if (table.hasValue(row, OUTPUT_1_COMPONENT + offset)) {
                    component = componentPatchOf(table.getValue(row, OUTPUT_1_COMPONENT + offset), registries);
                } else {
                    component = DataComponentPatch.EMPTY;
                }
                var item = new ItemStack(holder, count, component);
                builder.output(i, item);
            }
            var consumeMaterial = table.getBool(row, CONSUME_MATERIAL, true);
            builder.consume(consumeMaterial);
            builder.save(output, name);
        }
    }
}
