package com.zg.natural_transmute.utils;

import com.mojang.datafixers.util.Either;
import com.zg.natural_transmute.NaturalTransmute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class IdUtils {
    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(NaturalTransmute.MOD_ID, path);
    }

    public static ResourceLocation mcLoc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static <T> Either<ResourceKey<T>, TagKey<T>> idOrTag(ResourceKey<? extends Registry<T>> key, String str) {
        if (isTag(str)) {
            return Either.right(tag(key, str));
        } else {
            return Either.left(id(key, str));
        }
    }

    public static <T> TagKey<T> tag(ResourceKey<? extends Registry<T>> key, String str) {
        var id = id(str.substring(1));
        return TagKey.create(key, id);
    }

    public static <T> ResourceKey<T> id(ResourceKey<? extends Registry<T>> key, String str) {
        var id = id(str);
        return ResourceKey.create(key, id);
    }

    public static boolean isTag(String str) {
        return str.startsWith("#");
    }

    public static ResourceLocation id(String str) {
        return ResourceLocation.parse(str);
    }

    public static String string(ResourceLocation id) {
        return id.toString();
    }

    public static <T> String string(ResourceKey<T> key) {
        return string(key.location());
    }

    public static <T> String string(TagKey<T> key) {
        return "#" + string(key.location());
    }

    public static <T> String string(Either<ResourceKey<T>, TagKey<T>> idOrTag) {
        return idOrTag.map(IdUtils::string, IdUtils::string);
    }

    public static String normalized(String id) {
        return id.replace("#", "__").replace(":", "_");
    }

    public static <T> String normalized(ResourceLocation id) {
        return normalized(string(id));
    }

    public static <T> String normalized(ResourceKey<T> key) {
        return normalized(string(key));
    }

    public static <T> String normalized(TagKey<T> key) {
        return normalized(string(key));
    }

    public static <T> String normalized(Either<ResourceKey<T>, TagKey<T>> idOrTag) {
        return normalized(string(idOrTag));
    }
}
