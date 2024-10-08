package com.zg.natural_transmute.common.blocks;

import com.mojang.serialization.MapCodec;
import com.zg.natural_transmute.common.blocks.entity.HarmoniousChangeStoveBlockEntity;
import com.zg.natural_transmute.common.blocks.state.properties.HCStovePart;
import com.zg.natural_transmute.registry.NTBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HarmoniousChangeStove extends BaseEntityBlockWithState {

    public static final EnumProperty<HCStovePart> PART = EnumProperty.create("stove_part", HCStovePart.class);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HarmoniousChangeStove() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().pushReaction(PushReaction.IGNORE));
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, HCStovePart.MAIN)
                .setValue(HALF, DoubleBlockHalf.LOWER).setValue(LIT, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends HarmoniousChangeStove> codec() {
        return simpleCodec(p -> new HarmoniousChangeStove());
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf ? state.setValue(FACING, neighborState.getValue(FACING)) : Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            switch (state.getValue(PART)) {
                case MAIN -> {
                    BlockPos newPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                    serverPlayer.openMenu(state.getMenuProvider(level, newPos), extraData -> extraData.writeBlockPos(newPos));
                }
                case MAIN_HEAD -> {
                    BlockPos newPos1 = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                    BlockPos newPos2 = newPos1.relative(state.getValue(FACING).getCounterClockWise());
                    serverPlayer.openMenu(state.getMenuProvider(level, newPos2), extraData -> extraData.writeBlockPos(newPos2));
                }
                case RIGHT -> {
                    BlockPos newPos1 = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                    BlockPos newPos2 = newPos1.relative(state.getValue(FACING));
                    serverPlayer.openMenu(state.getMenuProvider(level, newPos2), extraData -> extraData.writeBlockPos(newPos2));
                }
                case RIGHT_HEAD -> {
                    BlockPos newPos1 = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                    BlockPos newPos2 = newPos1.relative(state.getValue(FACING)).relative(state.getValue(FACING).getCounterClockWise());
                    serverPlayer.openMenu(state.getMenuProvider(level, newPos2), extraData -> extraData.writeBlockPos(newPos2));
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            if (state.getValue(PART) == HCStovePart.MAIN && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, pos, Block.getId(state));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof HarmoniousChangeStoveBlockEntity blockEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
                BlockPos mainPos = blockEntity.mainPos;
                if (mainPos != null) {
                    Containers.dropContents(level, mainPos, blockEntity);
                    Direction facing = state.getValue(FACING).getOpposite();
                    BlockPos otherPos = mainPos.above().relative(facing).relative(facing.getCounterClockWise());
                    BlockPos.betweenClosed(mainPos, otherPos).forEach(tempPos -> level.removeBlock(tempPos, Boolean.FALSE));
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos mainPos = context.getClickedPos();
        BlockState blockState = super.getStateForPlacement(context);
        if (blockState == null) return null;
        Direction facing = blockState.getValue(FACING).getCounterClockWise();
        BlockPos backPos = mainPos.relative(facing.getOpposite());
        BlockPos sidePos = mainPos.relative(facing.getCounterClockWise());
        BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
        boolean placeable = this.canPlace(context.getLevel(),
                backPos, sidePos, diagonalPos, diagonalPos.above());
        return placeable ? blockState : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide()) {
            Direction facing = state.getValue(FACING).getCounterClockWise();
            BlockPos backPos = pos.relative(facing.getOpposite());
            BlockPos sidePos = pos.relative(facing.getCounterClockWise());
            BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
            if (this.canPlace(level, backPos, sidePos, diagonalPos, diagonalPos.above())) {
                level.setBlock(backPos, state.setValue(PART, HCStovePart.MAIN_HEAD)
                        .setValue(FACING, facing.getClockWise()), Block.UPDATE_ALL);
                level.setBlock(sidePos, state.setValue(PART, HCStovePart.RIGHT)
                        .setValue(FACING, facing.getClockWise()), Block.UPDATE_ALL);
                level.setBlock(diagonalPos, state.setValue(PART, HCStovePart.RIGHT_HEAD)
                        .setValue(FACING, facing.getClockWise()), Block.UPDATE_ALL);
                List.of(pos, backPos, sidePos, diagonalPos, diagonalPos.above()).forEach(tempPos -> {
                    if (level.getBlockEntity(tempPos) instanceof HarmoniousChangeStoveBlockEntity be) {
                        be.mainPos = pos;
                    }
                });
            }
        }
    }

    private boolean canPlace(Level level, BlockPos... blockPoses) {
        for (BlockPos blockPos : blockPoses) {
            if (!level.getBlockState(blockPos).canBeReplaced()) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, HALF, LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(PART) == HCStovePart.MAIN && state.getValue(HALF) == DoubleBlockHalf.LOWER ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HarmoniousChangeStoveBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, NTBlockEntityTypes.HARMONIOUS_CHANGE_STOVE.get(), HarmoniousChangeStoveBlockEntity::serverTick);
    }

}