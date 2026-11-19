package com.zg.natural_transmute.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KeyOfCollection extends Item {
    public KeyOfCollection(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        if (!level.isLoaded(pos)) {
            return InteractionResult.PASS;
        }
        var block = level.getBlockState(pos);
        if (!block.hasProperty(BlockStateProperties.VAULT_STATE)) {
            return InteractionResult.PASS;
        }
        var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof VaultBlockEntity vault)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }

        if (block.getValue(BlockStateProperties.VAULT_STATE) != VaultState.ACTIVE) {
            return InteractionResult.PASS;
        }
        var data = vault.getServerData();
        assert data != null;
        if (data.getRewardedPlayers().isEmpty()) {
            VaultBlockEntity.Server.playInsertFailSound(serverLevel, data, pos, SoundEvents.VAULT_INSERT_ITEM_FAIL);
            return InteractionResult.FAIL;
        }
        var stack = context.getItemInHand();
        var player = context.getPlayer();
        vault.getServerData().getRewardedPlayers().clear();
        stack.consume(1, player);
        return InteractionResult.SUCCESS;
    }
}
