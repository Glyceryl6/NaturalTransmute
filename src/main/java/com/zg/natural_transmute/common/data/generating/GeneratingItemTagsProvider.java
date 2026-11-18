package com.zg.natural_transmute.common.data.generating;

import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.common.data.generating.format.IGeneratingTable;
import com.zg.natural_transmute.utils.IdUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.zg.natural_transmute.utils.IdUtils.modLoc;

public class GeneratingItemTagsProvider extends ItemTagsProvider {
    protected final Map<TagKey<Item>, Set<Either<ResourceKey<Item>, TagKey<Item>>>> tags = new HashMap<>();

    public GeneratingItemTagsProvider(PackOutput output,
                                      CompletableFuture<HolderLookup.Provider> lookupProvider,
                                      CompletableFuture<TagsProvider.TagLookup<Item>> parentProvider,
                                      CompletableFuture<TagLookup<Block>> blockTags,
                                      String modId,
                                      @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, parentProvider, blockTags, modId, existingFileHelper);
    }

    private Set<Either<ResourceKey<Item>, TagKey<Item>>> ensureTag(TagKey<Item> key) {
        return tags.computeIfAbsent(key, __ -> new HashSet<>());
    }

    private void add(TagKey<Item> key, Either<ResourceKey<Item>, TagKey<Item>> value) {
        ensureTag(key).add(value);
    }

    private void addAll(TagKey<Item> key, Set<Either<ResourceKey<Item>, TagKey<Item>>> values) {
        ensureTag(key).addAll(values);
    }

    public void fromTable(IGeneratingTable table) {
        for (var row = 0; row < table.getRows(); row++) {
            if (!table.hasValue(row, 0)) {
                continue;
            }
            var key = table.getTag(Registries.ITEM, row, 0);
            for (var column = 1; column < table.getColumns(); column++) {
                if (!table.hasValue(row, column)) {
                    continue;
                }
                var value = table.getIdOrTag(Registries.ITEM, row, column);
                add(key, value);
            }
        }
    }

    public TagKey<Item> anonymousTag(Set<Either<ResourceKey<Item>, TagKey<Item>>> values) {
        var uniqueName = getTagHashCode(values);
        var key = TagKey.create(Registries.ITEM, modLoc("generating_tags/unique_key_" + uniqueName));

        addAll(key, values);
        return key;
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (var e : tags.entrySet()) {
            var tag = tag(e.getKey());
            e.getValue().forEach(
                ei -> ei.ifLeft(tag::add)
                    .ifRight(tag::addTag));
        }
    }

    @Override
    public String getName() {
        return modId + ": Generating Item Tags";
    }

    public static int getTagHashCode(Set<Either<ResourceKey<Item>, TagKey<Item>>> values) {
        // Ensure order insensitive string set.
        var strValues = values.stream().map(IdUtils::string).collect(Collectors.toSet());
        return strValues.hashCode();
    }
}
