package com.zg.natural_transmute.common.blocks.base;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StairBlockWithBase extends StairBlock implements IHasBaseBlock {

    private final Block base;

    public StairBlockWithBase(BlockState base, Properties properties) {
        super(base, properties);
        this.base = base.getBlock();
    }

    @Override
    public Block getBase() {
        return this.base;
    }

}