package com.zg.natural_transmute.common.data.provider.tag;

import com.google.common.collect.Lists;
import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.data.tags.NTBlockTags;
import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.utils.BlockPredicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NTBlockTagsProvider extends BlockTagsProvider {

    public NTBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NaturalTransmute.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
            NTBlocks.GATHERING_PLATFORM.get(),
            NTBlocks.HARMONIOUS_CHANGE_STOVE.get(),
            NTBlocks.PAPYRUS.get(), NTBlocks.CORUNDUM.get(),
            NTBlocks.HETEROGENEOUS_STONE_ORE.get(),
            NTBlocks.DEEPSLATE_HETEROGENEOUS_STONE_ORE.get(),
            NTBlocks.BLUE_NETHER_BRICKS.get(),
            NTBlocks.ALGAL_END_STONE.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(NTBlocks.BLUE_TARO_VINE.get(),
            NTBlocks.SIMULATED_RAMBLER.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(NTBlocks.TURQUOISE.get(),
            NTBlocks.HETEROGENEOUS_STONE_ORE.get(),
            NTBlocks.DEEPSLATE_HETEROGENEOUS_STONE_ORE.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(NTBlocks.CORUNDUM.get(),
            NTBlocks.HARMONIOUS_CHANGE_STOVE.get());
        this.tag(BlockTags.DIRT).add(NTBlocks.CAVE_EARTH.get(),
            NTBlocks.GRASSLAND_EARTH.get(), NTBlocks.OCEAN_EARTH.get());
        this.tag(BlockTags.CLIMBABLE).add(NTBlocks.BLUE_TARO_VINE.get());
        this.tag(BlockTags.LOGS).add(NTBlocks.PLANTAIN_STEM.get()).addTag(NTBlockTags.END_ALSOPHILA_LOGS);
        this.tag(BlockTags.PLANKS).add(NTBlocks.END_ALSOPHILA_PLANKS.get());
        this.tag(BlockTags.LEAVES).add(NTBlocks.END_ALSOPHILA_LEAVES.get(), NTBlocks.PLANTAIN_LEAVES.get());
        this.tag(BlockTags.SAPLINGS).add(NTBlocks.PLANTAIN_SAPLING.value(), NTBlocks.END_ALSOPHILA_SAPLING.get());
        this.tag(BlockTags.SWORD_EFFICIENT).add(NTBlocks.BLUEBERRY_BUSH.get(),
            NTBlocks.BLUE_TARO_VINE.get(), NTBlocks.SIMULATED_RAMBLER.get());
        this.tag(BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH).add(NTBlocks.BLUE_TARO_VINE.get());
        this.tag(BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH).add(NTBlocks.BLUE_TARO_VINE.get());
        this.tag(BlockTags.REPLACEABLE_BY_TREES).add(NTBlocks.BLUE_TARO_VINE.get(), NTBlocks.SIMULATED_RAMBLER.get());
        this.tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS).add(NTBlocks.SIMULATED_RAMBLER.get());
        this.tag(BlockTags.FALL_DAMAGE_RESETTING).add(NTBlocks.BLUEBERRY_BUSH.get());
        this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(NTBlocks.END_ALSOPHILA_FAMILY.getFirst().get());
        this.tag(BlockTags.FENCE_GATES).add(NTBlocks.END_ALSOPHILA_FAMILY.get(1).get());
        this.tag(BlockTags.WOODEN_TRAPDOORS).add(NTBlocks.END_ALSOPHILA_FAMILY.get(2).get());
        this.tag(BlockTags.WOODEN_BUTTONS).add(NTBlocks.END_ALSOPHILA_FAMILY.get(3).get());
        this.tag(BlockTags.WOODEN_DOORS).add(NTBlocks.END_ALSOPHILA_FAMILY.get(4).get());
        this.tag(BlockTags.WOODEN_STAIRS).add(NTBlocks.END_ALSOPHILA_FAMILY.get(5).get());
        this.tag(BlockTags.WOODEN_FENCES).add(NTBlocks.END_ALSOPHILA_FAMILY.get(6).get());
        this.tag(BlockTags.WOODEN_SLABS).add(NTBlocks.END_ALSOPHILA_FAMILY.getLast().get());
        this.tag(BlockTags.STAIRS).add(NTBlocks.BLUE_NETHER_BRICK_STAIRS.get());
        this.tag(BlockTags.SLABS).add(NTBlocks.BLUE_NETHER_BRICK_STAIRS.get());
        this.tag(BlockTags.WALLS).add(NTBlocks.BLUE_NETHER_BRICK_WALL.get());
        this.tag(NTBlockTags.PEAT_MOSS_PLACEABLE).add(Blocks.STONE, Blocks.DIRT, Blocks.MUD, Blocks.CLAY);
        this.tag(NTBlockTags.END_ALSOPHILA_LOGS).add(NTBlocks.STRIPPED_END_ALSOPHILA_WOOD.get(),
            NTBlocks.STRIPPED_END_ALSOPHILA_LOG.get(), NTBlocks.END_ALSOPHILA_WOOD.get(), NTBlocks.END_ALSOPHILA_LOG.get());
        this.tag(NTBlockTags.END_ALSOPHILA_SAPLING_PLACEABLE).add(Blocks.END_STONE, NTBlocks.ALGAL_END_STONE.get());
        NTBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get)
            .filter(block -> block instanceof BaseCoralPlantTypeBlock).toList()
            .forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block));

        {
            var bushes = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> BlockPredicates.IS_BUSH_BLOCK.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
            tag(NTBlockTags.BUSH).addAll(bushes);
        }

        {
            var infestedHost = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> BlockPredicates.IS_INFESTED_HOST.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
            tag(NTBlockTags.INFESTED_HOST).addAll(infestedHost);

            var infestedBlock = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> BlockPredicates.IS_INFESTED_BLOCK.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .toList();
            tag(NTBlockTags.INFESTED_BLOCK).addAll(infestedBlock);
        }
    }

}