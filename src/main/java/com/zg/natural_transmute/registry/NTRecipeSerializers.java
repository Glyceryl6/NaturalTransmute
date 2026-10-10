package com.zg.natural_transmute.registry;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.items.crafting.GatheringRecipe;
import com.zg.natural_transmute.common.items.crafting.GatheringSerializer;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeRecipe;
import com.zg.natural_transmute.common.items.crafting.HarmoniousChangeSerializer;
import com.zg.natural_transmute.common.items.crafting.special.HCRefrigeratedRocketRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, NaturalTransmute.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GatheringRecipe>> GATHERING_SERIALIZER =
        RECIPE_SERIALIZERS.register("gathering", () -> new GatheringSerializer<>(GatheringRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HarmoniousChangeRecipe>> HARMONIOUS_CHANGE_SERIALIZER =
        RECIPE_SERIALIZERS.register("harmonious_change", () -> new HarmoniousChangeSerializer<>(HarmoniousChangeRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCRefrigeratedRocketRecipe>> HC_REFRIGERATED_ROCKET_SERIALIZER =
        RECIPE_SERIALIZERS.register("hc_refrigerated_rocket", () -> new HarmoniousChangeSerializer<>(HCRefrigeratedRocketRecipe::new));

}
