package com.zg.natural_transmute.common.data.generating.format;

import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.utils.IdUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IGeneratingTable {
    int getRows();

    int getColumns();

    boolean hasValue(int row, int column);

    String getValue(int row, int column);

    default String getValue(int row, int column, String defaultValue) {
        if (!hasValue(row, column)) {
            return defaultValue;
        }
        return getValue(row, column);
    }

    int getNumber(int row, int column);

    default int getNumber(int row, int column, int defaultValue) {
        if (!hasValue(row, column)) {
            return defaultValue;
        }
        return getNumber(row, column);
    }

    int getPositiveNumber(int row, int column);

    default int getPositiveNumber(int row, int column, int defaultValue) {
        if (!hasValue(row, column)) {
            return defaultValue;
        }
        return getPositiveNumber(row, column);
    }

    boolean getBool(int row, int column);

    default boolean getBool(int row, int column, boolean defaultValue) {
        if (!hasValue(row, column)) {
            return defaultValue;
        }
        return getBool(row, column);
    }

    ResourceLocation getId(int row, int column);

    default <T> ResourceKey<T> getId(ResourceKey<? extends Registry<T>> key, int row, int column) {
        var value = getId(row, column);
        return ResourceKey.create(key, value);
    }

    <T> TagKey<T> getTag(ResourceKey<? extends Registry<T>> key, int row, int column);

    <T> Either<ResourceKey<T>, TagKey<T>> getIdOrTag(ResourceKey<? extends Registry<T>> key, int row, int column);
}
