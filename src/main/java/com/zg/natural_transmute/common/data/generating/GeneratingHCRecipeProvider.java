package com.zg.natural_transmute.common.data.generating;

import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.common.data.generating.format.IGeneratingTable;
import com.zg.natural_transmute.utils.IdUtils;
import lombok.SneakyThrows;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class GeneratingHCRecipeProvider extends RecipeProvider {
    protected final String modId;
    protected final IGeneratingTable table;
    protected final GeneratingItemTagsProvider tags;

    public GeneratingHCRecipeProvider(PackOutput output,
                                      CompletableFuture<HolderLookup.Provider> registries,
                                      String modId,
                                      IGeneratingTable table,
                                      GeneratingItemTagsProvider tags) {
        super(output, registries);
        this.modId = modId;
        this.table = table;
        this.tags = tags;
    }

    @Override
    public abstract String getName();

    // region Helpers

    protected static HolderLookup.RegistryLookup<Item> lookupOf(HolderLookup.Provider registries) {
        return registries.lookupOrThrow(Registries.ITEM);
    }

    protected static Holder<Item> holderOf(ResourceKey<Item> key, HolderLookup.Provider registries) {
        return lookupOf(registries).getOrThrow(key);
    }

    protected static Item itemOf(ResourceKey<Item> key, HolderLookup.Provider registries) {
        return holderOf(key, registries).value();
    }

    protected static boolean shouldCreateAnonymousTag(String s) {
        return s.contains(",");
    }

    protected Either<ResourceKey<Item>, TagKey<Item>> idOrCreatedTag(String s) {
        if (shouldCreateAnonymousTag(s)) {
            var values = Arrays.stream(s.split(","))
                .map(t -> IdUtils.idOrTag(Registries.ITEM, t))
                .collect(Collectors.toSet());
            return Either.right(tags.anonymousTag(values));
        } else {
            return IdUtils.idOrTag(Registries.ITEM, s);
        }
    }

    protected Ingredient ingredientOf(Either<ResourceKey<Item>, TagKey<Item>> key, HolderLookup.Provider registries) {
        return key.mapLeft(k -> itemOf(k, registries))
            .map(Ingredient::of, Ingredient::of);
    }

    protected ItemPredicate.Builder itemPredicateBuilderOf(Either<ResourceKey<Item>, TagKey<Item>> key, HolderLookup.Provider registries) {
        return key.mapLeft(l -> itemOf(l, registries))
            .map(
                l -> ItemPredicate.Builder.item().of(l),
                r -> ItemPredicate.Builder.item().of(r)
            );
    }

    @SneakyThrows
    protected DataComponentPatch componentPatchOf(String s, HolderLookup.Provider registries) {
        Tag tag = TagParser.parseTag(s);
        return DataComponentPatch.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
            .getOrThrow(IllegalArgumentException::new);
    }

    protected DataComponentPredicate componentPredicateOf(DataComponentPatch patch) {
        return DataComponentPredicate.allOf(patch.split().added());
    }

    protected HolderSet<Item> holderSetOf(Either<ResourceKey<Item>, TagKey<Item>> key, HolderLookup.Provider registries) {
        return key.mapLeft(l -> holderOf(l, registries))
            .map(
                HolderSet::direct,
                BuiltInRegistries.ITEM::getOrCreateTag
            );
    }

    // endregion
}
