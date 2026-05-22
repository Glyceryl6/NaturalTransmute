package com.zg.natural_transmute.utils;

import net.minecraft.core.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class NonNullListUtils {
    public static <E> List<E> trimDefault(NonNullList<E> list, E defaultValue) {
        var result = new ArrayList<E>();
        for (var e : list) {
            if (!e.equals(defaultValue)) {
                result.add(e);
            }
        }
        return result;
    }

    public static <E> NonNullList<E> withSizeAndDefault(List<E> list, int size, E defaultValue) {
        var result = NonNullList.withSize(size, defaultValue);
        for (var i = 0; i < Math.min(size, list.size()); i++) {
            result.set(i, list.get(i));
        }
        return result;
    }
}
