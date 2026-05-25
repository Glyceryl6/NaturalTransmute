package com.zg.natural_transmute.registry;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.items.crafting.*;
import com.zg.natural_transmute.common.items.crafting.special.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, NaturalTransmute.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GatheringRecipe>> GATHERING_SERIALIZER =
            RECIPE_SERIALIZERS.register("gathering", () -> new GatheringSerializer<>(GatheringRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HarmoniousChangeRecipe>> HARMONIOUS_CHANGE_SERIALIZER =
            RECIPE_SERIALIZERS.register("harmonious_change", HarmoniousChangeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCBlockFamilyTransferRecipe>> HC_BLOCK_FAMILY_TRANSFER_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_block_family_transfer", HCBlockFamilyTransferSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCUnglazedTerracottaRecipe>> HC_UNGLAZED_TERRACOTTA_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_unglazed_terracotta", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCUnglazedTerracottaRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCCreateInfestedBlockRecipe>> HC_CREATE_INFESTED_BLOCK_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_create_infested_block", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCCreateInfestedBlockRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCRecycleInfestedBlockRecipe>> HC_RECYCLE_INFESTED_BLOCK_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_recycle_infested_block", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCRecycleInfestedBlockRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCRefrigeratedRocketRecipe>> HC_REFRIGERATED_ROCKET_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_refrigerated_rocket", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCRefrigeratedRocketRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCMelodiousDiscRecipe>> HC_MELODIOUS_DISC_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_melodious_disc", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCMelodiousDiscRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCLeaderBannerRecipe>> HC_LEADER_BANNER_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_leader_banner", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCLeaderBannerRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCSuspiciousStewRecipe>> HC_SUSPICIOUS_STEW_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_suspicious_stew", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCSuspiciousStewRecipe()));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HCHeroicEnchantedBookRecipe>> HC_HEROIC_ENCHANTED_BOOK_SERIALIZER =
            RECIPE_SERIALIZERS.register("hc_heroic_enchanted_book", () -> new HCBiomeCatalystOnlySerializer<>(ingredient -> new HCHeroicEnchantedBookRecipe()));

}