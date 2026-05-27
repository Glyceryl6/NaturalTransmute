package com.zg.natural_transmute.utils;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.InfestedBlock;

import java.util.function.Predicate;

public class BlockPredicates {
    public static final Predicate<Block> IS_BUSH_BLOCK = block -> block instanceof BushBlock && block.asItem() != Items.AIR;
    public static final Predicate<Block> IS_INFESTED_HOST = InfestedBlock.BLOCK_BY_HOST_BLOCK::containsKey;
    public static final Predicate<Block> IS_INFESTED_BLOCK = block -> block instanceof InfestedBlock;
}
