package com.zg.natural_transmute.common.data.tags;

import com.zg.natural_transmute.NaturalTransmute;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class NTBlockTags {

    public static final TagKey<Block> PEAT_MOSS_PLACEABLE = create("peat_moss_placeable");
    public static final TagKey<Block> END_ALSOPHILA_LOGS = create("end_alsophila_logs");
    public static final TagKey<Block> END_ALSOPHILA_SAPLING_PLACEABLE = create("end_alsophila_sapling_placeable");
    public static final TagKey<Block> BUSH = create("bush");

    private static TagKey<Block> create(String name) {
        return BlockTags.create(NaturalTransmute.prefix(name));
    }

}