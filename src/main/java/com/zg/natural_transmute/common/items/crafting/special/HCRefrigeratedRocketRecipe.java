package com.zg.natural_transmute.common.items.crafting.special;

import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipeInput;
import com.zg.natural_transmute.registry.NTRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;
import java.util.Optional;

/**
 * Copies the fireworks component of the rocket in the first input slot onto the first output.
 */
public class HCRefrigeratedRocketRecipe extends HarmoniousChangeRecipe {
    public HCRefrigeratedRocketRecipe(
        List<Optional<SizedIngredient>> inputs, Ingredient biomeCatalyst,
        List<ItemStack> outputs, int time, boolean consume) {
        super(inputs, biomeCatalyst, outputs, time, consume);
    }

    @Override
    public List<ItemStack> assembleOutputs(HarmoniousChangeRecipeInput input, HolderLookup.Provider registries) {
        List<ItemStack> results = super.assembleOutputs(input, registries);
        Fireworks fireworks = input.getItem(0).get(DataComponents.FIREWORKS);
        ItemStack resultStack = results.getFirst();
        if (fireworks != null && !resultStack.isEmpty()) {
            resultStack.set(DataComponents.FIREWORKS, fireworks);
        }

        return results;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NTRecipeSerializers.HC_REFRIGERATED_ROCKET_SERIALIZER.get();
    }
}
