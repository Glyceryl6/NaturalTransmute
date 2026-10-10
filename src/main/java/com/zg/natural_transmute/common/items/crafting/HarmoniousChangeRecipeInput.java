package com.zg.natural_transmute.common.items.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * @param inputs the three input slots in order, empty slots are kept as {@link ItemStack#EMPTY}.
 */
public record HarmoniousChangeRecipeInput(List<ItemStack> inputs, ItemStack biomeCatalyst) implements RecipeInput {

    public static final int BIOME_CATALYST_INDEX = HarmoniousChangeRecipe.INPUT_SLOTS;

    @Override
    public ItemStack getItem(int index) {
        if (index < HarmoniousChangeRecipe.INPUT_SLOTS) {
            return this.inputs.get(index);
        } else if (index == BIOME_CATALYST_INDEX) {
            return this.biomeCatalyst;
        } else {
            throw new IllegalArgumentException("No item for index " + index);
        }
    }

    @Override
    public int size() {
        return HarmoniousChangeRecipe.INPUT_SLOTS + 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inputs.stream().allMatch(ItemStack::isEmpty);
    }

}
