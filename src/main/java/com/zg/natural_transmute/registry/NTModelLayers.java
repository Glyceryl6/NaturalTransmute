package com.zg.natural_transmute.registry;

import com.zg.natural_transmute.NaturalTransmute;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class NTModelLayers {

    public static final ModelLayerLocation GATHERING_PLATFORM = register("gathering_platform");
    public static final ModelLayerLocation MOOBLOOM = register("moobloom");
    public static final ModelLayerLocation DUCK = register("duck");

    private static ModelLayerLocation register(String path) {
        return register(path, "main");
    }

    private static ModelLayerLocation register(String path, String layer) {
        return new ModelLayerLocation(NaturalTransmute.prefix(path), layer);
    }

}