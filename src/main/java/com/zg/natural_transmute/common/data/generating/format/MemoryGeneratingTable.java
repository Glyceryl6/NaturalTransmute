package com.zg.natural_transmute.common.data.generating.format;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.utils.IdUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

public abstract class MemoryGeneratingTable implements IGeneratingTable {
    protected final Table<Integer, Integer, String> table = HashBasedTable.create();

    protected int rowsTotal = 0;
    protected int columnsTotal = 0;

    @Override
    public int getRows() {
        return rowsTotal;
    }

    @Override
    public int getColumns() {
        return columnsTotal;
    }

    @Override
    public boolean hasValue(int row, int column) {
        return table.contains(row, column);
    }

    @Override
    public String getValue(int row, int column) {
        @Nullable
        var value = table.get(row, column);
        if (value == null) {
            throw new IllegalArgumentException(errorAt(row, column, "value is not present"));
        }
        return value;
    }

    @Override
    public int getNumber(int row, int column) {
        var value = getValue(row, column);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errorAt(row, column, "value is not a number"), ex);
        }
    }

    @Override
    public int getPositiveNumber(int row, int column) {
        var num = getNumber(row, column);
        if (num <= 0) {
            throw new IllegalArgumentException(errorAt(row, column, "number must be positive"));
        }
        return num;
    }

    @Override
    public boolean getBool(int row, int column) {
        var value = getValue(row, column);
        return Boolean.parseBoolean(value);
    }

    @Override
    public ResourceLocation getId(int row, int column) {
        var value = getValue(row, column);
        try {
            return IdUtils.id(value);
        } catch (ResourceLocationException ex) {
            throw new IllegalArgumentException(errorAt(row, column, "value is not a valid ResourceLocation"), ex);
        }
    }

    @Override
    public <T> TagKey<T> getTag(ResourceKey<? extends Registry<T>> key, int row, int column) {
        var value = getValue(row, column);
        if (!IdUtils.isTag(value)) {
            throw new IllegalArgumentException(errorAt(row, column, "value is not a TagKey"));
        }
        try {
            return IdUtils.tag(key, value);
        } catch (ResourceLocationException ex) {
            throw new IllegalArgumentException(errorAt(row, column, "value is not a valid TagKey"), ex);
        }
    }

    @Override
    public <T> Either<ResourceKey<T>, TagKey<T>> getIdOrTag(ResourceKey<? extends Registry<T>> key, int row, int column) {
        try {
            var value = getValue(row, column);
            return IdUtils.idOrTag(key, value);
        } catch (ResourceLocationException ex) {
            throw new IllegalArgumentException(errorAt(row, column, "value is neither a ResourceLocation nor a TagKey"), ex);
        }
    }

    private static String errorAt(int row, int column, String message) {
        return "At " + row + ", " + column + ": " + message;
    }
}
