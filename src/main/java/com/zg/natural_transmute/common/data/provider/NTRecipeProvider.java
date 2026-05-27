package com.zg.natural_transmute.common.data.provider;

import com.google.common.collect.Sets;
import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.blocks.ActivatedCoralWallFan;
import com.zg.natural_transmute.common.data.NTBlockFamilies;
import com.zg.natural_transmute.common.data.recipes.GatheringRecipeBuilder;
import com.zg.natural_transmute.common.data.recipes.HCBlockFamilyTransferRecipeBuilder;
import com.zg.natural_transmute.common.data.recipes.HarmoniousChangeRecipeBuilder;
import com.zg.natural_transmute.common.data.tags.NTBlockTags;
import com.zg.natural_transmute.common.data.tags.NTItemTags;
import com.zg.natural_transmute.common.items.WaterWax;
import com.zg.natural_transmute.common.items.crafting.special.*;
import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.registry.NTItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.*;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class NTRecipeProvider extends RecipeProvider {

    private static final List<BlockFamily> WOOD_BLOCK_FAMILIES = List.of(
        BlockFamilies.ACACIA_PLANKS, BlockFamilies.CHERRY_PLANKS,
        BlockFamilies.BIRCH_PLANKS, BlockFamilies.CRIMSON_PLANKS,
        BlockFamilies.JUNGLE_PLANKS, BlockFamilies.OAK_PLANKS,
        BlockFamilies.DARK_OAK_PLANKS, BlockFamilies.SPRUCE_PLANKS,
        BlockFamilies.WARPED_PLANKS, BlockFamilies.MANGROVE_PLANKS);

    public NTRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        this.buildArmorRecipes(output, NTItems.DRAGONCAST_HELMET.get(), NTItems.DRAGONCAST_CHESTPLATE.get(),
            NTItems.DRAGONCAST_LEGGINGS.get(), NTItems.DRAGONCAST_BOOTS.get(), NTItems.DRAGONCAST_STEEL_INGOT.get());
        this.buildBaseToolRecipes(output, NTItems.SCULK_BONE_SWORD.get(), NTItems.SCULK_BONE_PICKAXE.get(), NTItems.SCULK_BONE_SHOVEL.get(),
            NTItems.SCULK_BONE_AXE.get(), NTItems.SCULK_BONE_HOE.get(), Blocks.DEEPSLATE.asItem(), NTItems.SCULK_BONE.get());
        NTBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(blockFamily ->
            generateRecipes(output, blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA)));
        threeByThreePacker(output, RecipeCategory.BUILDING_BLOCKS, Blocks.WARPED_WART_BLOCK, NTItems.WARPED_WART.get());
        nineBlockStorageRecipes(output, RecipeCategory.MISC, NTItems.AMBER.get(),
            RecipeCategory.BUILDING_BLOCKS, NTBlocks.AMBER_BLOCK.get());
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 6)
            .requires(NTItems.WHALE_BONE.get()).group("bonemeal")
            .unlockedBy("has_whale_bone", has(NTItems.WHALE_BONE.get()))
            .save(output, NaturalTransmute.prefix("whale_bone_to_bonemeal"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.HARMONIOUS_CHANGE_FUEL.get())
            .requires(Ingredient.of(Items.AMETHYST_SHARD, Items.LAPIS_LAZULI)).requires(ItemTags.COALS)
            .unlockedBy("has_coals", has(ItemTags.COALS)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.CORUNDUM_IRON_PLATE.get())
            .requires(Ingredient.of(NTBlocks.CORUNDUM.get(), Items.IRON_INGOT))
            .unlockedBy("has_corundum", has(NTBlocks.CORUNDUM.get())).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, NTBlocks.BLUE_NETHER_BRICKS.get())
            .requires(NTItems.WARPED_WART.get(), 2).requires(Items.NETHER_BRICK, 2)
            .unlockedBy(getHasName(NTItems.WARPED_WART.get()), has(NTItems.WARPED_WART.get()))
            .unlockedBy(getHasName(Items.NETHER_BRICK), has(Items.NETHER_BRICK)).save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE)
            .requires(NTItems.FLOWERING_BLUE_TARO_VINE.get())
            .unlockedBy(getHasName(NTItems.FLOWERING_BLUE_TARO_VINE.get()),
                has(NTItems.FLOWERING_BLUE_TARO_VINE.get()))
            .save(output, NaturalTransmute.prefix("blue_taro_vine_to_light_blue_dye"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PUMPKIN_PIE)
            .requires(Blocks.PUMPKIN).requires(Items.SUGAR).requires(NTItems.DUCK_EGG.get())
            .unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN))
            .unlockedBy("has_pumpkin", has(Blocks.PUMPKIN))
            .save(output, NaturalTransmute.prefix("duck_egg_to_pumpkin_pie"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PAPER).requires(NTBlocks.PLANTAIN_STEM.get())
            .unlockedBy(getHasName(NTBlocks.PLANTAIN_STEM.get()), has(NTBlocks.PLANTAIN_STEM.get()))
            .save(output, NaturalTransmute.prefix("plantain_stem_to_paper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTBlocks.HARMONIOUS_CHANGE_STOVE.get())
            .define('A', Items.COPPER_INGOT).define('B', Items.AMETHYST_SHARD)
            .define('C', NTItems.CORUNDUM_IRON_PLATE.get()).define('D', Items.HOPPER)
            .define('E', NTItems.HARMONIOUS_CHANGE_CORE.get()).define('F', Items.BLAST_FURNACE)
            .pattern("ADA").pattern("BEB").pattern("CFC")
            .unlockedBy(getHasName(NTItems.HARMONIOUS_CHANGE_CORE.get()),
                has(NTItems.HARMONIOUS_CHANGE_CORE.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 3)
            .define('#', Blocks.SUGAR_CANE).pattern("###")
            .unlockedBy("has_papyrus", has(NTItems.PAPYRUS.get()))
            .save(output, NaturalTransmute.prefix("papyrus_to_paper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUNGLE_PLANKS)
            .define('#', NTItems.COCONUT_SHELL.get()).pattern("##").pattern("##")
            .unlockedBy("has_coconut_shell", has(NTItems.COCONUT_SHELL.get()))
            .save(output, NaturalTransmute.prefix("coconut_shell_to_jungle_plank"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, NTItems.WHALE_BONE_BOW.get())
            .define('#', NTItems.WHALE_BONE.get()).define('X', Items.STRING)
            .pattern(" #X").pattern("# X").pattern(" #X")
            .unlockedBy("has_string", has(Items.STRING)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTItems.HARMONIOUS_CHANGE_CORE.get())
            .define('H', NTItems.HETEROGENEOUS_STONE.get()).define('I', Items.DIAMOND)
            .pattern(" H ").pattern("HIH").pattern(" H ")
            .unlockedBy(getHasName(NTItems.HETEROGENEOUS_STONE.get()),
                has(NTItems.HETEROGENEOUS_STONE.get())).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
            .define('#', Items.STICK)
            .define('X', NTItems.AMBER.get())
            .pattern("X").pattern("#")
            .unlockedBy(getHasName(NTItems.AMBER.get()), has(NTItems.AMBER.get()))
            .save(output, NaturalTransmute.prefix("torch_from_amber"));
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
            .define('A', Items.MILK_BUCKET)
            .define('B', Items.SUGAR)
            .define('C', Items.WHEAT)
            .define('E', NTItems.DUCK_EGG.get())
            .pattern("AAA").pattern("BEB").pattern("CCC")
            .unlockedBy(getHasName(NTItems.DUCK_EGG.get()), has(NTItems.DUCK_EGG.get()))
            .save(output, NaturalTransmute.prefix("duck_egg_to_cake"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NTItems.DUCK.get()), RecipeCategory.FOOD,
                NTItems.COOKED_DUCK.get(), 0.35F, 200)
            .unlockedBy(getHasName(NTItems.DUCK.get()), has(NTItems.DUCK.get())).save(output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NTItems.DRAGONCAST_STEEL_BILLET.get()), RecipeCategory.MISC,
                NTItems.DRAGONCAST_STEEL_INGOT.get(), 0.35F, 200)
            .unlockedBy(getHasName(NTItems.DRAGONCAST_STEEL_BILLET.get()),
                has(NTItems.DRAGONCAST_STEEL_BILLET.get())).save(output);
        // 聚相台配方
        gathering(output, Items.SAND, Items.SAND, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_DESERT.get(), 160);
        gathering(output, Items.RED_SAND, Items.RED_SAND, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_BADLANDS.get(), 160);
        gathering(output, Items.OAK_SAPLING, Items.OAK_SAPLING, NTItems.H_BADLANDS.get(), NTItems.H_WOODED_BADLANDS.get(), 320);
        gathering(output, Items.SNOW_BLOCK, Items.SNOW_BLOCK, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_SNOWY_PLAINS.get(), 160);
        gathering(output, Items.PACKED_ICE, Items.PACKED_ICE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_ICE_SPIKES.get(), 160);
        gathering(output, Ingredient.of(Items.GRAVEL, Items.STONE, Items.ANDESITE, Items.GRANITE, Items.DIORITE),
            Ingredient.of(Items.EMERALD), NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_MOUNTAINS.get(), 160);
        gathering(output, Items.SEAGRASS, Items.GRAVEL, NTItems.H_OCEAN.get(), NTItems.H_STONE_SHORE.get(), 320);
        gathering(output, Items.BLUE_ICE, Items.POWDER_SNOW_BUCKET, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), 320);
        gathering(output, Ingredient.of(Items.GRASS_BLOCK), Ingredient.of(ItemTags.FLOWERS),
            NTItems.H_MOUNTAINS.get(), NTItems.H_MEADOW.get(), 320);
        gathering(output, Ingredient.of(Items.OAK_SAPLING, Items.OAK_LEAVES, Items.OAK_LOG),
            Ingredient.of(Items.OAK_SAPLING, Items.OAK_LEAVES, Items.OAK_LOG),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_FOREST.get(), 160);
        gathering(output, Ingredient.of(ItemTags.FLOWERS), Ingredient.of(ItemTags.FLOWERS),
            NTItems.H_FOREST.get(), NTItems.H_FLOWER_FOREST.get(), 320);
        gathering(output, Ingredient.of(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LEAVES, Items.DARK_OAK_LOG),
            Ingredient.of(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LEAVES, Items.DARK_OAK_LOG),
            NTItems.H_FOREST.get(), NTItems.H_DARK_FOREST.get(), 320);
        gathering(output, Ingredient.of(Items.BIRCH_SAPLING, Items.BIRCH_LEAVES, Items.BIRCH_LOG),
            Ingredient.of(Items.BIRCH_SAPLING, Items.BIRCH_LEAVES, Items.BIRCH_LOG),
            NTItems.H_FOREST.get(), NTItems.H_BIRCH_FOREST.get(), 320);
        gathering(output, Items.MYCELIUM, Items.MYCELIUM, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_MUSHROOM.get(), 160);
        gathering(output, Items.SLIME_BALL, Items.SLIME_BALL, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_SWAMP.get(), 160);
        gathering(output, Ingredient.of(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LEAVES, Items.MANGROVE_LOG),
            Ingredient.of(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LEAVES, Items.MANGROVE_LOG),
            NTItems.H_SWAMP.get(), NTItems.H_MANGROVE_SWAMP.get(), 320);
        gathering(output, Ingredient.of(ItemTags.VILLAGER_PLANTABLE_SEEDS), Ingredient.of(Items.DIRT),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_PLAINS.get(), 160);
        gathering(output, Ingredient.of(Items.JUNGLE_SAPLING, Items.JUNGLE_LEAVES, Items.JUNGLE_LOG),
            Ingredient.of(Items.JUNGLE_SAPLING, Items.JUNGLE_LEAVES, Items.JUNGLE_LOG),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_JUNGLE.get(), 160);
        gathering(output, Ingredient.of(Items.ACACIA_SAPLING, Items.ACACIA_LEAVES, Items.ACACIA_LOG),
            Ingredient.of(Items.ACACIA_SAPLING, Items.ACACIA_LEAVES, Items.ACACIA_LOG),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_SAVANNA.get(), 160);
        gathering(output, Items.CLAY, Items.WATER_BUCKET, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_RIVER.get(), 160);
        gathering(output, Items.KELP, Items.WATER_BUCKET, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_OCEAN.get(), 160);
        gathering(output, new BlockTagIngredient(BlockTags.ICE).toVanilla(),
            new BlockTagIngredient(BlockTags.ICE).toVanilla(),
            NTItems.H_OCEAN.get(), NTItems.H_FROZEN_OCEAN.get(), 320);
        gathering(output, Items.KELP, Items.TURTLE_SCUTE, NTItems.H_OCEAN.get(), NTItems.H_BEACH.get(), 320);
        gathering(output, new BlockTagIngredient(BlockTags.CORAL_PLANTS).toVanilla(),
            new BlockTagIngredient(BlockTags.CORALS).toVanilla(),
            NTItems.H_OCEAN.get(), NTItems.H_WARM_OCEAN.get(), 320);
        gathering(output, Ingredient.of(Items.CHERRY_SAPLING, Items.CHERRY_LEAVES, Items.CHERRY_LOG),
            Ingredient.of(Items.CHERRY_SAPLING, Items.CHERRY_LEAVES, Items.CHERRY_LOG),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_CHERRY_GROVE.get(), 160);
        gathering(output, Items.DEEPSLATE, Items.DEEPSLATE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_DEEPSLATE.get(), 160);
        gathering(output, Items.MOSS_BLOCK, Items.MOSS_BLOCK, NTItems.H_DEEPSLATE.get(), NTItems.H_LUSH_CAVE.get(), 320);
        gathering(output, Items.DRIPSTONE_BLOCK, Items.DRIPSTONE_BLOCK, NTItems.H_DEEPSLATE.get(), NTItems.H_DRIPSTONE_CAVES.get(), 320);
        gathering(output, Items.SCULK, Items.SCULK, NTItems.H_DEEPSLATE.get(), NTItems.H_DEEP_DARK.get(), 320);
        gathering(output, Ingredient.of(Items.SPRUCE_SAPLING, Items.SPRUCE_LEAVES, Items.SPRUCE_LOG),
            Ingredient.of(Items.SPRUCE_SAPLING, Items.SPRUCE_LEAVES, Items.SPRUCE_LOG),
            NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_TAIGA.get(), 160);
        gathering(output, Items.PODZOL, Items.MOSSY_COBBLESTONE, NTItems.H_TAIGA.get(), NTItems.H_OLD_GROWTH_TAIGA.get(), 160);
        gathering(output, Items.WIND_CHARGE, Items.WIND_CHARGE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_WINDSWEPT.get(), 160);
        gathering(output, Items.NETHERRACK, Items.NETHERRACK, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_NETHER.get(), 160);
        gathering(output, Items.CRIMSON_FUNGUS, Items.CRIMSON_ROOTS, NTItems.H_NETHER.get(), NTItems.H_CRIMSON_FOREST.get(), 320);
        gathering(output, Items.WARPED_FUNGUS, Items.WARPED_ROOTS, NTItems.H_NETHER.get(), NTItems.H_WARPED_FOREST.get(), 320);
        gathering(output, Items.SOUL_SAND, Items.BONE, NTItems.H_NETHER.get(), NTItems.H_SOUL_SAND_VALLEY.get(), 320);
        gathering(output, Items.BASALT, Items.MAGMA_CREAM, NTItems.H_NETHER.get(), NTItems.H_BASALT_DELTAS.get(), 320);
        gathering(output, Items.END_STONE, Items.CHORUS_FRUIT, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_END_HIGHLANDS.get(), 160);
        gathering(output, Items.DRAGON_BREATH, Items.DRAGON_BREATH, NTItems.HETEROGENEOUS_STONE.get(), NTItems.H_END.get(), 160);
        // 谐变炉配方
        AtomicInteger hDesertIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Ingredient.of(Items.RED_SANDSTONE, Items.STONE, Items.COBBLESTONE, Items.DEEPSLATE, Items.COBBLED_DEEPSLATE))
            .results(Items.SANDSTONE), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.SANDSTONE).results(Items.SANDSTONE, 4), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.MUD).results(Items.DIRT), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.DIRT).results(Items.COARSE_DIRT), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Ingredient.of(Items.GRAVEL, Items.COARSE_DIRT, Items.RED_SAND))
            .results(Items.SAND), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Ingredient.of(Items.GRASS_BLOCK, Items.PODZOL, Items.MYCELIUM))
            .results(Items.DIRT), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(ItemTags.SAPLINGS).results(Items.DEAD_BUSH), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.WET_SPONGE).results(Items.SPONGE), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.SAND, 2).results(Items.QUARTZ), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.SUGAR_CANE).results(NTItems.PAPYRUS.get()), hDesertIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_PERSIAN.get()), hDesertIndex.incrementAndGet());

        AtomicInteger hBadlandsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Ingredient.of(Items.SANDSTONE, Items.STONE, Items.COBBLESTONE, Items.DEEPSLATE, Items.COBBLED_DEEPSLATE))
            .results(Items.RED_SANDSTONE), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.MUD).results(Items.DIRT), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Ingredient.of(Items.GRASS_BLOCK, Items.PODZOL, Items.MYCELIUM))
            .results(Items.DIRT), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Ingredient.of(Items.GRAVEL, Items.COARSE_DIRT, Items.SAND))
            .results(Items.RED_SAND), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(ItemTags.SAPLINGS).results(Items.DEAD_BUSH), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.WET_SPONGE).results(Items.SPONGE), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.RED_SAND, 2).results(Items.QUARTZ)
            .results(Items.GOLD_NUGGET), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.IRON_NUGGET).results(Items.GOLD_NUGGET), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Ingredient.of(Items.COPPER_INGOT, Items.IRON_INGOT))
            .results(Items.GOLD_INGOT), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.TURTLE_SCUTE).results(Items.ARMADILLO_SCUTE), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.MELON_SLICE).results(Items.GLISTERING_MELON_SLICE), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.CARROT, 2).requires(Items.GOLD_INGOT)
            .results(Items.GOLDEN_CARROT), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.APPLE).requires(Items.GOLD_INGOT, 2)
            .results(Items.GOLDEN_APPLE), hBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BADLANDS.get())
            .requires(Items.GUNPOWDER, 2).requires(Items.PAPER)
            .results(NTItems.PETARD.get()), hBadlandsIndex.incrementAndGet());

        AtomicInteger hWoodedBadlandsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Ingredient.of(Items.SAND, Items.RED_SAND)).results(Items.DIRT), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.DEAD_BUSH).results(Items.OAK_SAPLING), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS).results(Items.GRASS_BLOCK), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.ROTTEN_FLESH).results(Items.BONE), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.TURTLE_SCUTE).results(Items.ARMADILLO_SCUTE), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.WHEAT_SEEDS).requires(Items.WHEAT).results(Items.CARROT), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.CARROT).results(Items.POTATO), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.MELON_SLICE).results(Items.GLISTERING_MELON_SLICE), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.CARROT, 2).requires(Items.GOLD_INGOT)
            .results(Items.GOLDEN_CARROT), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.APPLE).requires(Items.GOLD_INGOT, 2)
            .results(Items.GOLDEN_APPLE), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.IRON_NUGGET).results(Items.GOLD_NUGGET), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Ingredient.of(Items.COPPER_INGOT, Items.IRON_INGOT))
            .results(Items.GOLD_INGOT), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Items.APPLE).requires(Items.CACTUS).results(NTItems.PITAYA.get()), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Tags.Items.FOODS_COOKED_MEAT).results(NTItems.DOGFOOD_STRIPED.get()), hWoodedBadlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WOODED_BADLANDS.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_RAGDOLL.get()), hWoodedBadlandsIndex.incrementAndGet());

        AtomicInteger hSnowyPlainsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(Items.SNOW_BLOCK, 2).requires(Items.CARVED_PUMPKIN)
            .results(Items.SNOWBALL, 16).consume(false), hSnowyPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(Items.WATER_BUCKET).results(Items.ICE), hSnowyPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(Items.MAGMA_BLOCK).results(Items.OBSIDIAN), hSnowyPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS).results(Items.GRASS_BLOCK), hSnowyPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hSnowyPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_PLAINS.get())
            .requires(ItemTags.SMALL_FLOWERS).results(NTItems.DRYAS_OCTOPETALA.get()), hSnowyPlainsIndex.incrementAndGet());

        AtomicInteger hIceSpikesIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Items.SNOW_BLOCK, 2).requires(Items.CARVED_PUMPKIN)
            .results(Items.SNOWBALL, 16).consume(false), hIceSpikesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Items.WATER_BUCKET).requires(Items.ICE).results(Items.PACKED_ICE), hIceSpikesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Items.WATER_BUCKET).results(Items.ICE), hIceSpikesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hIceSpikesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hIceSpikesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_ICE_SPIKES.get())
            .requires(Ingredient.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION))
            .results(NTItems.VODKA.get()), hIceSpikesIndex.incrementAndGet());

        AtomicInteger hMountainsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get())
            .requires(Items.STONE).results(Items.COBBLESTONE), hMountainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get())
            .requires(Items.DEEPSLATE).results(Items.COBBLED_DEEPSLATE), hMountainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get())
            .requires(Ingredient.of(Items.COBBLESTONE, Items.COBBLED_DEEPSLATE))
            .results(Items.GRAVEL), hMountainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hMountainsIndex.incrementAndGet());

        AtomicInteger hSnowySlopesIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Items.SNOW_BLOCK, 2).requires(Items.CARVED_PUMPKIN)
            .results(Items.SNOWBALL, 16).consume(false), hSnowySlopesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Ingredient.of(Items.WATER_BUCKET, Items.POWDER_SNOW_BUCKET))
            .results(Items.ICE), hSnowySlopesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Items.WATER_BUCKET).requires(Items.ICE).results(Items.PACKED_ICE), hSnowySlopesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hSnowySlopesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Items.BUCKET).requires(Items.SNOW_BLOCK).results(Items.POWDER_SNOW_BUCKET), hSnowySlopesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SNOWY_SLOPES.get())
            .requires(Items.STONE_BRICKS).results(Items.CRACKED_STONE_BRICKS), hSnowySlopesIndex.incrementAndGet());

        AtomicInteger hMeadowIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.RAW_IRON).requires(Items.QUARTZ)
            .results(Items.AMETHYST_SHARD, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.BONE).requires(Items.STONE).results(Items.CALCITE), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS)
            .results(Items.GRASS_BLOCK), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.BLUE_DYE)
            .results(Items.CORNFLOWER, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.LIGHT_GRAY_DYE, 2)
            .results(Items.AZURE_BLUET, 3), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.YELLOW_DYE)
            .results(Items.DANDELION, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.RED_DYE)
            .results(Items.POPPY, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.MAGENTA_DYE)
            .results(Items.ALLIUM, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.SHORT_GRASS).requires(Items.LIGHT_GRAY_DYE)
            .results(Items.OXEYE_DAISY, 2), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.HONEYCOMB_BLOCK).results(Items.HONEY_BLOCK), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.HONEY_BLOCK).results(Items.HONEYCOMB_BLOCK), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.HONEYCOMB).requires(Items.GLASS_BOTTLE)
            .results(Items.HONEY_BOTTLE), hMeadowIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MEADOW.get())
            .requires(Items.HONEY_BOTTLE).results(Items.HONEYCOMB), hMeadowIndex.incrementAndGet());

        AtomicInteger hForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.OAK_SAPLING)
            .results(Items.OAK_SAPLING), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Items.OAK_SAPLING, 3).results(Items.BIRCH_SAPLING, 3), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(ItemTags.LEAVES).excepts(Items.OAK_LEAVES)
            .results(Items.OAK_LEAVES), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Items.OAK_LEAVES).requires(Items.BIRCH_SAPLING)
            .results(Items.BIRCH_LEAVES), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(
                new Ingredient.TagValue(NTItemTags.VEGETABLE),
                new Ingredient.TagValue(NTItemTags.FRUIT))))
            .results(Items.APPLE), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Tags.Items.FOODS_COOKED_MEAT).results(NTItems.DOGFOOD_WOODS.get()), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).requires(Items.WHITE_DYE)
            .results(NTItems.CAT_FOOD_WHITE.get()), hForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FOREST.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_BRITISH_SHORTHAIR.get()), hForestIndex.incrementAndGet());

        AtomicInteger hFlowerForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.OAK_SAPLING)
            .results(Items.OAK_SAPLING), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.OAK_SAPLING).results(Items.BIRCH_SAPLING), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.BIRCH_SAPLING).results(Items.OAK_SAPLING), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.OAK_LEAVES).results(Items.BIRCH_LEAVES), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(ItemTags.LEAVES).excepts(Items.OAK_LEAVES)
            .results(Items.OAK_LEAVES), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(
                new Ingredient.TagValue(NTItemTags.VEGETABLE),
                new Ingredient.TagValue(NTItemTags.FRUIT))))
            .results(Items.APPLE), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.BLUE_DYE)
            .results(Items.CORNFLOWER, 2), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.LIGHT_GRAY_DYE, 2)
            .results(Items.AZURE_BLUET, 3), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.YELLOW_DYE)
            .results(Items.DANDELION, 2), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.RED_DYE)
            .results(Items.POPPY, 2), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.MAGENTA_DYE)
            .results(Items.ALLIUM, 2), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS).requires(Items.LIGHT_GRAY_DYE)
            .results(Items.OXEYE_DAISY, 2), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS, 2).requires(Items.ORANGE_DYE)
            .results(Items.ORANGE_TULIP, 3), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS, 2).requires(Items.RED_DYE)
            .results(Items.RED_TULIP, 3), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS, 2).requires(Items.LIGHT_GRAY_DYE)
            .results(Items.WHITE_TULIP, 3), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.SHORT_GRASS, 2).requires(Items.PINK_DYE)
            .results(Items.PINK_TULIP, 3), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.TALL_GRASS).requires(Items.MAGENTA_DYE).results(Items.LILAC), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.TALL_GRASS).requires(Items.RED_DYE).results(Items.ROSE_BUSH), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.TALL_GRASS).requires(Items.PINK_DYE).results(Items.PEONY), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.HONEYCOMB_BLOCK).results(Items.HONEY_BLOCK), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.HONEY_BLOCK).results(Items.HONEYCOMB_BLOCK), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.HONEYCOMB).requires(Items.GLASS_BOTTLE)
            .results(Items.HONEY_BOTTLE), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.HONEY_BOTTLE).results(Items.HONEYCOMB), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(Items.DANDELION).results(NTBlocks.BUTTERCUP.get()), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FLOWER_FOREST.get())
            .requires(NTBlocks.BUTTERCUP.get(), 2).requires(Items.WHEAT)
            .results(NTItems.MOO_BLOOM_STRANGE_FEED.get()), hFlowerForestIndex.incrementAndGet());

        AtomicInteger hDarkForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.DARK_OAK_SAPLING)
            .results(Items.DARK_OAK_SAPLING), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(ItemTags.LEAVES).excepts(Items.DARK_OAK_LEAVES)
            .results(Items.DARK_OAK_LEAVES), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(
                new Ingredient.TagValue(NTItemTags.VEGETABLE),
                new Ingredient.TagValue(NTItemTags.FRUIT))))
            .results(Items.APPLE), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.EXPERIENCE_BOTTLE).results(Items.OMINOUS_BOTTLE), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.LAPIS_LAZULI).requires(Items.GOLDEN_APPLE)
            .requires(new BlockTagIngredient(Tags.Blocks.SKULLS).toVanilla())
            .results(Items.TOTEM_OF_UNDYING), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.RED_MUSHROOM).requires(ItemTags.PLANKS)
            .results(Items.RED_MUSHROOM_BLOCK), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.BROWN_MUSHROOM).requires(ItemTags.PLANKS)
            .results(Items.BROWN_MUSHROOM_BLOCK), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(ItemTags.PLANKS).requires(ItemTags.LOGS)
            .results(Items.MUSHROOM_STEM, 4), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.EMERALD).requires(Items.BONE).results(NTItems.FANGS.get()), hDarkForestIndex.incrementAndGet());

        AtomicInteger hBirchForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.BIRCH_SAPLING)
            .results(Items.BIRCH_SAPLING), hBirchForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(ItemTags.LEAVES).excepts(Items.BIRCH_LEAVES)
            .results(Items.BIRCH_LEAVES), hDarkForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_TABBY.get()), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).requires(Items.ORANGE_DYE)
            .results(NTItems.CAT_FOOD_TABBY.get()), hFlowerForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BIRCH_FOREST.get())
            .requires(ItemTags.SAPLINGS).requires(Items.GLASS_BOTTLE)
            .results(NTItems.BOTTLE_OF_TEA.get()), hFlowerForestIndex.incrementAndGet());

        AtomicInteger hMushroomIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(ItemTags.SMALL_FLOWERS).results(Items.RED_MUSHROOM), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Ingredient.of(Items.SHORT_GRASS, Items.FERN))
            .results(Items.BROWN_MUSHROOM), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.GRASS_BLOCK).results(Items.MYCELIUM), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.BOWL).results(Items.MUSHROOM_STEW), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Ingredient.of(Items.VINE, Items.MOSS_CARPET))
            .results(Items.GLOW_LICHEN), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.RED_MUSHROOM).requires(ItemTags.PLANKS)
            .results(Items.RED_MUSHROOM_BLOCK), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.BROWN_MUSHROOM).requires(ItemTags.PLANKS)
            .results(Items.BROWN_MUSHROOM_BLOCK), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(ItemTags.PLANKS).requires(ItemTags.LOGS)
            .results(Items.MUSHROOM_STEM, 4), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.BROWN_MUSHROOM).requires(Items.GOLDEN_APPLE)
            .results(NTItems.GANODERMA_LUCIDUM.get()), hMushroomIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MUSHROOM.get())
            .requires(Items.WHEAT).requires(Items.FERMENTED_SPIDER_EYE).requires(Items.SUGAR)
            .results(NTItems.MOOSHROOM_STRANGE_FEED.get()), hMushroomIndex.incrementAndGet());

        AtomicInteger hSwampIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.OAK_SAPLING)
            .results(Items.OAK_SAPLING), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(ItemTags.LEAVES).excepts(Items.OAK_LEAVES)
            .results(Items.OAK_LEAVES), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS)
            .results(Items.GRASS_BLOCK), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Ingredient.of(Items.BIG_DRIPLEAF, Items.SMALL_DRIPLEAF))
            .results(Items.LILY_PAD), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Items.SHORT_GRASS).results(Items.VINE), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(ItemTags.FLOWERS).results(Items.BLUE_ORCHID), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Items.HONEY_BLOCK).results(Items.SLIME_BLOCK), hSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get())
            .requires(Items.APPLE).requires(Items.SUGAR_CANE).results(NTItems.PINEAPPLE.get()), hSwampIndex.incrementAndGet());

        AtomicInteger hMangroveSwampIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.MANGROVE_PROPAGULE)
            .results(Items.MANGROVE_PROPAGULE), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(ItemTags.LEAVES).excepts(Items.MANGROVE_LEAVES)
            .results(Items.MANGROVE_LEAVES), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.MAGMA_CREAM).requires(Items.BAMBOO)
            .results(Items.PEARLESCENT_FROGLIGHT), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.MAGMA_CREAM).requires(Items.BROWN_MUSHROOM)
            .results(Items.OCHRE_FROGLIGHT), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.MAGMA_CREAM).requires(Items.SNOWBALL)
            .results(Items.VERDANT_FROGLIGHT), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.MAGMA_CREAM).results(NTBlocks.AZURE_FROGLIGHT.get()), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.TROPICAL_FISH_BUCKET).requires(Items.SLIME_BLOCK)
            .results(Items.TADPOLE_BUCKET), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.DIRT).results(Items.MUD), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.GLOW_LICHEN).results(Items.MOSS_CARPET), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.SHORT_GRASS).results(Items.VINE), hMangroveSwampIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MANGROVE_SWAMP.get())
            .requires(Items.MUD).results(NTItems.PEAT_MOSS.get()), hMangroveSwampIndex.incrementAndGet());

        AtomicInteger hPlainsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.PORKCHOP).results(Items.BEEF), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.BEEF).results(Items.CHICKEN), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.CHICKEN).results(Items.PORKCHOP), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.COOKED_PORKCHOP).results(Items.COOKED_BEEF), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.COOKED_BEEF).results(Items.COOKED_CHICKEN), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.COOKED_CHICKEN).results(Items.COOKED_PORKCHOP), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Tags.Items.SEEDS).requires(Items.APPLE).results(Items.PUMPKIN_SEEDS), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.WHEAT_SEEDS).requires(Items.WHEAT).results(Items.CARROT), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.CARROT).results(Items.POTATO), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(ItemTags.TALL_FLOWERS).requires(Items.YELLOW_DYE)
            .results(Items.SUNFLOWER), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS).results(Items.GRASS_BLOCK), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).requires(Items.BLACK_DYE)
            .results(NTItems.CAT_FOOD_TUXEDO.get()), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).requires(Items.BONE)
            .results(NTItems.CAT_FOOD_JELLIE.get()), hPlainsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_CALICO.get()), hPlainsIndex.incrementAndGet());

        AtomicInteger hJungleIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(ItemTags.SAPLINGS).excepts(Items.JUNGLE_SAPLING)
            .results(Items.JUNGLE_SAPLING), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(ItemTags.LEAVES).excepts(Items.JUNGLE_LEAVES)
            .results(Items.JUNGLE_LEAVES), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(Items.GRASS_BLOCK).requires(Items.ROTTEN_FLESH)
            .results(Items.PODZOL), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_PLAINS.get())
            .requires(Tags.Items.SEEDS).requires(Items.JUNGLE_LOG)
            .results(Items.COCOA_BEANS), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(Items.JUNGLE_SAPLING).results(NTItems.PLANTAIN.get()), hJungleIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_JUNGLE.get())
            .requires(Tags.Items.FOODS_COOKED_FISH).results(NTItems.CAT_FOOD_SIAMESE.get()), hJungleIndex.incrementAndGet());

        AtomicInteger hSavannaIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SAVANNA.get())
            .requires(ItemTags.WOOL).requires(Items.MUTTON).results(Items.LEATHER), hSavannaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SAVANNA.get())
            .requires(Items.WATER_BUCKET).requires(Items.BEEF).results(Items.MILK_BUCKET), hSavannaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SAVANNA.get())
            .requires(Items.GRASS_BLOCK).requires(Items.LEATHER).requires(Items.GOLDEN_APPLE)
            .results(NTBlocks.GRASSLAND_EARTH.get()), hSavannaIndex.incrementAndGet());

        AtomicInteger hRiverIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.GRAVEL).results(Items.SAND), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.COARSE_DIRT).results(Items.DIRT), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.POISONOUS_POTATO).results(Items.POTATO), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(ItemTags.WOOL).results(Items.WHITE_WOOL), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(ItemTags.BEDS).results(Items.WHITE_BED), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(ItemTags.BANNERS).results(Items.WHITE_BANNER), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(ItemTags.WOOL_CARPETS).results(Items.WHITE_CARPET), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.MAGMA_BLOCK).results(Items.OBSIDIAN), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.EGG).results(NTItems.DUCK_EGG.get()), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.CHICKEN).results(NTItems.DUCK.get()), hRiverIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_RIVER.get())
            .requires(Items.LILY_PAD).requires(Items.SHORT_GRASS).results(NTItems.REED.get()), hRiverIndex.incrementAndGet());

        AtomicInteger hOceanIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).results(Items.PRISMARINE_SHARD, 8), hOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).requires(Items.LAPIS_LAZULI)
            .results(Items.PRISMARINE_CRYSTALS, 8), hOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get())
            .requires(Items.WATER_BUCKET).results(Items.POWDER_SNOW_BUCKET), hOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get())
            .requires(ItemTags.LEAVES).results(Items.KELP), hOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get())
            .requires(Items.HEART_OF_THE_SEA).requires(Items.DIRT)
            .results(NTBlocks.OCEAN_EARTH.get()), hOceanIndex.incrementAndGet());

        AtomicInteger hFrozenOceanIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).results(Items.PRISMARINE_SHARD, 8), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).requires(Items.LAPIS_LAZULI)
            .results(Items.PRISMARINE_CRYSTALS, 8), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.SNOW_BLOCK, 2).requires(Items.CARVED_PUMPKIN)
            .results(Items.SNOWBALL, 16).consume(false), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.WATER_BUCKET).requires(Items.ICE).results(Items.PACKED_ICE), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.WATER_BUCKET).requires(Items.PACKED_ICE)
            .results(Items.BLUE_ICE), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Ingredient.of(Items.WATER_BUCKET, Items.SNOWBALL))
            .results(Items.ICE), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Tags.Items.BUCKETS_ENTITY_WATER).results(Items.COD_BUCKET), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(DataComponentIngredient.of(true, PotionContents.createItemStack(Items.POTION, Potions.WATER)))
            .results(Items.SNOWBALL).results(Items.GLASS_BOTTLE), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.HONEY_BOTTLE).requires(Ingredient.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION))
            .results(NTItems.HONEY_WINE.get(), 2), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.BONE).requires(Items.BEEF).results(NTItems.WHALE_BONE.get()), hFrozenOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_FROZEN_OCEAN.get())
            .requires(Items.LAVA_BUCKET).requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
            .results(Items.BASALT).consume(false), hFrozenOceanIndex.incrementAndGet());

        AtomicInteger hBeachIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Tags.Items.RAW_MATERIALS).results(Items.PRISMARINE_SHARD, 8), hBeachIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Tags.Items.RAW_MATERIALS).requires(Items.LAPIS_LAZULI)
            .results(Items.PRISMARINE_CRYSTALS, 8), hBeachIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Items.ARMADILLO_SCUTE).results(Items.TURTLE_SCUTE), hBeachIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Items.EGG).results(Items.TURTLE_EGG), hBeachIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Items.TURTLE_SCUTE).requires(Items.BONE)
            .results(Items.NAUTILUS_SHELL), hBeachIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BEACH.get())
            .requires(Items.SUGAR_CANE).requires(ItemTags.LOGS)
            .results(Items.ARMADILLO_SCUTE), hBeachIndex.incrementAndGet());

        AtomicInteger hStoneShoreIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Tags.Items.RAW_MATERIALS).results(Items.PRISMARINE_SHARD, 8), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Tags.Items.RAW_MATERIALS).requires(Items.LAPIS_LAZULI)
            .results(Items.PRISMARINE_CRYSTALS, 8), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Items.COBBLESTONE).results(Items.GRAVEL), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Ingredient.of(Items.STONE, Items.BASALT))
            .results(Items.GRAVEL), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Items.DEEPSLATE).results(Items.COBBLED_DEEPSLATE), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(ItemTags.WOOL).results(Items.WHITE_WOOL), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(ItemTags.BEDS).results(Items.WHITE_BED), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(ItemTags.BANNERS).results(Items.WHITE_BANNER), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(ItemTags.WOOL_CARPETS).results(Items.WHITE_CARPET), hStoneShoreIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_STONE_SHORE.get())
            .requires(Items.NAUTILUS_SHELL).requires(Items.SPIDER_EYE)
            .results(NTItems.GOOSE_BARNACLE.get()), hStoneShoreIndex.incrementAndGet());
        BlockFamilies.getAllFamilies().filter(b -> b.getVariants().containsKey(BlockFamily.Variant.CRACKED)).forEach(blockFamily ->
            blockFamily.getVariants().forEach((variant, block) -> {
                Block resultBlock = blockFamily.get(BlockFamily.Variant.CRACKED);
                harmoniousChange(output, NTItems.H_STONE_SHORE.get(), block, resultBlock, hStoneShoreIndex.incrementAndGet());
            }));

        AtomicInteger hWarmOceanIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).results(Items.PRISMARINE_SHARD, 8), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Tags.Items.RAW_MATERIALS).requires(Items.LAPIS_LAZULI)
            .results(Items.PRISMARINE_CRYSTALS, 8), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Items.ARMADILLO_SCUTE).results(Items.TURTLE_SCUTE), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Items.TURTLE_SCUTE).requires(Items.BONE)
            .results(Items.NAUTILUS_SHELL), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Items.ARMADILLO_SCUTE).results(Items.TURTLE_SCUTE), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(ItemTags.FLOWERS).results(Items.SEA_PICKLE), hWarmOceanIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(Items.HONEYCOMB).requires(Items.PUFFERFISH)
            .results(NTItems.WATER_WAX.get()), hWarmOceanIndex.incrementAndGet());

        AtomicInteger hDeepslateIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.COBBLESTONE).results(Items.COBBLED_DEEPSLATE), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.COBBLED_DEEPSLATE).results(Items.GRAVEL), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.GRAVEL, 2).results(Items.TUFF), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.BONE).requires(Items.STONE).results(Items.CALCITE), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.BONE_BLOCK).results(Items.SKELETON_SKULL), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.SKELETON_SKULL).requires(Items.ROTTEN_FLESH)
            .results(Items.ZOMBIE_HEAD), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.SKELETON_SKULL).requires(Items.GUNPOWDER)
            .results(Items.CREEPER_HEAD), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(Items.ZOMBIE_HEAD).requires(Items.GOLDEN_APPLE).requires(Items.FERMENTED_SPIDER_EYE)
            .results(Items.PLAYER_HEAD), hDeepslateIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(ItemTags.COALS).results(NTItems.AMBER.get()), hDeepslateIndex.incrementAndGet());
        BlockFamilies.getAllFamilies().filter(b -> b.getVariants().containsKey(BlockFamily.Variant.POLISHED))
            .forEach(blockFamily -> blockFamily.getVariants().forEach((variant, block) -> harmoniousChange(output,
                NTItems.H_DEEPSLATE.get(), block, blockFamily.getBaseBlock(), hDeepslateIndex.incrementAndGet())));

        AtomicInteger hLushCaveIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.AZALEA)
            .results(Items.AZALEA), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.LEAVES).excepts(Items.AZALEA_LEAVES)
            .results(Items.AZALEA_LEAVES), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.FLOWERS).requires(Items.AZALEA)
            .results(Items.FLOWERING_AZALEA), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.FLOWERS).requires(Items.AZALEA_LEAVES)
            .results(Items.FLOWERING_AZALEA_LEAVES), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Ingredient.of(Items.SWEET_BERRIES, NTItems.BLUEBERRIES.get()))
            .results(Items.GLOW_BERRIES), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Ingredient.of(Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN))
            .results(Items.SMALL_DRIPLEAF), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.DIRT).results(Items.MOSS_BLOCK), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.DIRT).requires(Items.SHORT_GRASS)
            .results(Items.ROOTED_DIRT), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.FLOWERS).results(Items.SPORE_BLOSSOM), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.BONE_BLOCK).results(Items.SKELETON_SKULL), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.SKELETON_SKULL).requires(Items.ROTTEN_FLESH)
            .results(Items.ZOMBIE_HEAD), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.SKELETON_SKULL).requires(Items.GUNPOWDER)
            .results(Items.CREEPER_HEAD), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.ZOMBIE_HEAD).requires(Items.GOLDEN_APPLE).requires(Items.FERMENTED_SPIDER_EYE)
            .results(Items.PLAYER_HEAD), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.YELLOW_DYE)
            .results(createCustomAxolotlBucket(Axolotl.Variant.GOLD)), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.PINK_DYE)
            .results(createCustomAxolotlBucket(Axolotl.Variant.LUCY)), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.BROWN_DYE)
            .results(createCustomAxolotlBucket(Axolotl.Variant.WILD)), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.CYAN_DYE)
            .results(createCustomAxolotlBucket(Axolotl.Variant.CYAN)), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.BLUE_DYE).requires(Items.HEART_OF_THE_SEA)
            .results(createCustomAxolotlBucket(Axolotl.Variant.BLUE)), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(ItemTags.COALS).results(NTItems.AMBER.get()), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.VINE).requires(Items.PITCHER_PLANT)
            .results(NTBlocks.BLUE_TARO_VINE.get()), hLushCaveIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_LUSH_CAVE.get())
            .requires(Items.MOSS_BLOCK).requires(Items.GOLDEN_APPLE)
            .results(NTBlocks.CAVE_EARTH.get()), hLushCaveIndex.incrementAndGet());

        AtomicInteger hDripstoneCavesIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Ingredient.of(Items.COBBLESTONE, Items.COBBLED_DEEPSLATE))
            .results(Items.POINTED_DRIPSTONE), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Ingredient.of(Items.STONE, Items.DEEPSLATE))
            .results(Items.DRIPSTONE_BLOCK), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.MUD).results(Items.CLAY), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Ingredient.of(Items.LAVA_BUCKET, Items.MAGMA_BLOCK))
            .results(Items.OBSIDIAN), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.BONE_BLOCK).results(Items.SKELETON_SKULL), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.SKELETON_SKULL).requires(Items.ROTTEN_FLESH)
            .results(Items.ZOMBIE_HEAD), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.SKELETON_SKULL).requires(Items.GUNPOWDER)
            .results(Items.CREEPER_HEAD), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.ZOMBIE_HEAD).requires(Items.GOLDEN_APPLE).requires(Items.FERMENTED_SPIDER_EYE)
            .results(Items.PLAYER_HEAD), hDripstoneCavesIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DRIPSTONE_CAVES.get())
            .requires(Items.WATER_BUCKET).results(NTItems.MINE_WATER_BUCKET.get()), hDripstoneCavesIndex.incrementAndGet());

        AtomicInteger hDeepDarkIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(ItemTags.DIRT).results(Items.SCULK), hDeepDarkIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.GLOW_LICHEN).results(Items.SCULK_VEIN), hDeepDarkIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.BONE).results(NTItems.SCULK_BONE.get()), hDeepDarkIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.SCULK).requires(Items.REDSTONE).results(Items.SCULK_SENSOR), hDeepDarkIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.SCULK).requires(Items.GOAT_HORN).results(Items.SCULK_SHRIEKER), hDeepDarkIndex.incrementAndGet());

        AtomicInteger hTaigaIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_TAIGA.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.SPRUCE_SAPLING)
            .results(Items.SPRUCE_SAPLING), hTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_TAIGA.get())
            .requires(ItemTags.LEAVES).excepts(Items.SPRUCE_LEAVES)
            .results(Items.SPRUCE_LEAVES), hTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_TAIGA.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_TAIGA.get())
            .requires(Items.SWEET_BERRIES).results(NTItems.BLUEBERRIES.get()), hTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_TAIGA.get())
            .requires(Items.WHEAT_SEEDS).requires(Items.SUGAR).results(Items.BEETROOT), hTaigaIndex.incrementAndGet());

        AtomicInteger hOldGrowthTaigaIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(Ingredient.fromValues(Stream.of(new Ingredient.ItemValue(new ItemStack(Items.DEAD_BUSH)),
                new Ingredient.TagValue(ItemTags.SAPLINGS)))).excepts(Items.SPRUCE_SAPLING)
            .results(Items.SPRUCE_SAPLING), hOldGrowthTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(ItemTags.LEAVES).excepts(Items.SPRUCE_LEAVES)
            .results(Items.SPRUCE_LEAVES), hOldGrowthTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hOldGrowthTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(Items.SPIDER_EYE).results(Items.FERMENTED_SPIDER_EYE), hOldGrowthTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(Items.BROWN_MUSHROOM).results(NTItems.TRUFFLE.get()), hOldGrowthTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OLD_GROWTH_TAIGA.get())
            .requires(Items.GRASS_BLOCK).requires(Items.ROTTEN_FLESH)
            .results(Items.PODZOL), hOldGrowthTaigaIndex.incrementAndGet());

        AtomicInteger hCherryGroveTaigaIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(ItemTags.SAPLINGS).excepts(Items.CHERRY_SAPLING)
            .results(Items.CHERRY_SAPLING), hCherryGroveTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(Items.DIRT).results(Items.GRASS_BLOCK), hCherryGroveTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(Items.SHORT_GRASS).results(Items.DRIED_KELP), hCherryGroveTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(Ingredient.of(new ItemStack(Items.IRON_SWORD))).requires(Items.CHERRY_SAPLING)
            .results(NTItems.KATANA.get()), hCherryGroveTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(ItemTags.FLOWERS).results(Items.PINK_PETALS, 4), hCherryGroveTaigaIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CHERRY_GROVE.get())
            .requires(Ingredient.of(Items.CHISELED_STONE_BRICKS, Items.CHISELED_DEEPSLATE))
            .results(Items.CHISELED_TUFF), hCherryGroveTaigaIndex.incrementAndGet());

        AtomicInteger hWindsweptIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Items.STRING).results(Items.FEATHER), hWindsweptIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Items.FIRE_CHARGE).results(Items.WIND_CHARGE), hWindsweptIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Items.BLAZE_ROD).results(Items.BREEZE_ROD), hWindsweptIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Items.ARROW).results(NTItems.BREEZE_ARROW.get()), hWindsweptIndex.incrementAndGet());

        AtomicInteger hNetherIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Items.AMETHYST_BLOCK).requires(Items.MAGMA_BLOCK)
            .results(Items.GLOWSTONE, 2), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Ingredient.of(Items.STONE, Items.DEEPSLATE))
            .results(Items.NETHERRACK), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Ingredient.of(Items.ANDESITE, Items.DIORITE, Items.GRANITE, Items.TUFF, Items.CALCITE))
            .results(Items.BLACKSTONE), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Items.GLASS).results(Items.QUARTZ), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Ingredient.of(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM)).requires(Items.GHAST_TEAR)
            .results(Items.NETHER_WART), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Items.BREEZE_ROD).results(Items.BLAZE_ROD), hNetherIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_NETHER.get())
            .requires(Items.WIND_CHARGE).results(Items.FIRE_CHARGE), hNetherIndex.incrementAndGet());

        AtomicInteger hCrimsonForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CRIMSON_FOREST.get())
            .requires(Items.SKELETON_SKULL).requires(Items.PORKCHOP)
            .results(Items.PIGLIN_HEAD), hCrimsonForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CRIMSON_FOREST.get())
            .requires(Ingredient.of(Items.GLOWSTONE, Items.MAGMA_BLOCK)).requires(Items.WEEPING_VINES)
            .results(Items.SHROOMLIGHT), hCrimsonForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CRIMSON_FOREST.get())
            .requires(Ingredient.of(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM))
            .results(Items.CRIMSON_FUNGUS), hCrimsonForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CRIMSON_FOREST.get())
            .requires(Ingredient.of(Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN))
            .results(Items.CRIMSON_ROOTS), hCrimsonForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_CRIMSON_FOREST.get())
            .requires(ItemTags.FLOWERS).requires(Items.SHROOMLIGHT)
            .results(NTBlocks.SIMULATED_RAMBLER.get()), hCrimsonForestIndex.incrementAndGet());

        AtomicInteger hWarpedForestIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARPED_FOREST.get())
            .requires(Items.NETHER_WART).results(NTItems.WARPED_WART.get()), hWarpedForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARPED_FOREST.get())
            .requires(Items.OBSIDIAN).requires(Items.ENDER_PEARL)
            .results(Items.CRYING_OBSIDIAN), hWarpedForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARPED_FOREST.get())
            .requires(Ingredient.of(Items.GLOWSTONE, Items.MAGMA_BLOCK)).requires(Items.TWISTING_VINES)
            .results(Items.SHROOMLIGHT), hWarpedForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARPED_FOREST.get())
            .requires(Ingredient.of(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM))
            .results(Items.WARPED_FUNGUS), hWarpedForestIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARPED_FOREST.get())
            .requires(Ingredient.of(Items.SHORT_GRASS, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN))
            .results(Items.WARPED_ROOTS), hWarpedForestIndex.incrementAndGet());

        AtomicInteger hSoulSandVallyIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Ingredient.of(Items.SAND, Items.RED_SAND, Items.GRAVEL)).requires(Items.BONE)
            .results(Items.SOUL_SAND), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Ingredient.of(Items.DIRT, Items.COARSE_DIRT, Items.CLAY))
            .results(Items.SOUL_SOIL), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.TORCH).results(Items.SOUL_TORCH), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.LANTERN).results(Items.SOUL_LANTERN), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.CAMPFIRE).results(Items.SOUL_CAMPFIRE), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.SOUL_SOIL).requires(Items.GOLDEN_APPLE)
            .results(NTBlocks.DEATH_EARTH.get()), hSoulSandVallyIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.PACKED_ICE).requires(Items.LAVA_BUCKET)
            .results(Items.BASALT).consume(false), hSoulSandVallyIndex.incrementAndGet());

        AtomicInteger hBasaltDeltasIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_BASALT_DELTAS.get())
            .requires(Ingredient.of(Items.RAW_COPPER, Items.RAW_IRON))
            .results(Items.RAW_GOLD), hBasaltDeltasIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.SLIME_BALL).requires(Items.MAGMA_BLOCK)
            .results(Items.MAGMA_CREAM, 10), hBasaltDeltasIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Ingredient.of(Items.COBBLESTONE, Items.COBBLED_DEEPSLATE))
            .results(Items.MAGMA_BLOCK), hBasaltDeltasIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Ingredient.of(Items.OBSIDIAN, Items.CRYING_OBSIDIAN)).requires(Items.BUCKET)
            .results(Items.LAVA_BUCKET), hBasaltDeltasIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Items.AXOLOTL_BUCKET).requires(Items.MAGMA_BLOCK)
            .results(NTItems.LAVA_AXOLOTL_BUCKET.get()), hBasaltDeltasIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SOUL_SAND_VALLEY.get())
            .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL)).requires(Items.BLUE_ICE)
            .results(Items.BASALT).consume(false), hBasaltDeltasIndex.incrementAndGet());

        AtomicInteger hEndHightlandsIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Ingredient.of(Items.STONE, Items.DEEPSLATE))
            .results(Items.END_STONE), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.ENDER_PEARL).requires(Items.JUNGLE_SAPLING)
            .results(NTBlocks.END_ALSOPHILA_SAPLING.get()), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.ENDER_PEARL).requires(Items.BAMBOO)
            .results(Items.CHORUS_FRUIT), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.ENDER_PEARL).requires(Items.SPORE_BLOSSOM)
            .results(Items.CHORUS_FLOWER), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.QUARTZ).requires(Items.POPPED_CHORUS_FRUIT)
            .results(Items.ENDER_PEARL), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(ItemTags.LOGS).results(Items.PURPUR_BLOCK), hEndHightlandsIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.TORCH).results(Items.END_ROD), hEndHightlandsIndex.incrementAndGet());

        AtomicInteger hTheEndIndex = new AtomicInteger(0);
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(new BlockTagIngredient(Tags.Blocks.SKULLS).toVanilla()).requires(Items.END_CRYSTAL)
            .results(Items.DRAGON_HEAD), hTheEndIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(DataComponentIngredient.of(true, PotionContents.createItemStack(Items.POTION, Potions.WATER)))
            .requires(Items.CHORUS_FRUIT).results(Items.DRAGON_HEAD), hTheEndIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(Ingredient.of(Items.STONE, Items.DEEPSLATE))
            .results(Items.END_STONE), hTheEndIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(Items.AMETHYST_BLOCK).requires(Items.GHAST_TEAR)
            .results(Items.END_CRYSTAL), hTheEndIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(Items.SNIFFER_EGG).requires(Items.END_CRYSTAL).requires(Items.DRAGON_BREATH)
            .results(Items.DRAGON_EGG), hTheEndIndex.incrementAndGet());
        harmoniousChange(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END.get())
            .requires(NTItems.CORUNDUM_IRON_PLATE.get()).requires(Items.ANCIENT_DEBRIS).requires(Items.DRAGON_BREATH)
            .results(NTItems.DRAGONCAST_STEEL_BILLET.get()), hTheEndIndex.incrementAndGet());

        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get())
            .requires(Items.DIAMOND).requires(Items.CUT_SANDSTONE), Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.DIAMOND).requires(Items.STONE), Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DARK_FOREST.get())
            .requires(Items.DIAMOND).requires(Items.STONE), Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Items.DIAMOND).requires(Items.BREEZE_ROD), Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
            .requires(Ingredient.of(Items.COPPER_BLOCK, Items.CUT_COPPER))
            .requires(Items.DIAMOND), Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(
                NTItems.H_OCEAN.get(), NTItems.H_FROZEN_OCEAN.get(), NTItems.H_BEACH.get(),
                NTItems.H_STONE_SHORE.get(), NTItems.H_WARM_OCEAN.get())
            .requires(Items.DIAMOND).requires(Items.STONE), Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.DIAMOND).requires(Items.DEEPSLATE), Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(Items.DIAMOND).requires(Items.DEEPSLATE), Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.DIAMOND).requires(Items.PURPUR_BLOCK), Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_END_HIGHLANDS.get())
            .requires(Items.DIAMOND).requires(Items.END_STONE), Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);

        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.BREWER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.ARMS_UP_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.ARCHER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.MINER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.SKULL_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get()).requires(Items.BRICK), Items.PRIZE_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get()).requires(Items.BRICK), Items.MOURNER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get()).requires(Items.BRICK), Items.BLADE_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get()).requires(Items.BRICK), Items.PLENTY_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_OCEAN.get()).requires(Items.BRICK), Items.EXPLORER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get()).requires(Items.BRICK), Items.SNORT_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get()).requires(Items.BRICK), Items.SHELTER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get()).requires(Items.BRICK), Items.ANGLER_POTTERY_SHERD);

        ItemLike[] forestBiomeCatalyst = new ItemLike[]{NTItems.H_BIRCH_FOREST.get(), NTItems.H_JUNGLE.get(), NTItems.H_TAIGA.get(), NTItems.H_OLD_GROWTH_TAIGA.get()};
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.BURN_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.SHEAF_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.HOWL_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.HEART_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.HEARTBREAK_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.DANGER_POTTERY_SHERD);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(forestBiomeCatalyst).requires(Items.BRICK), Items.FRIEND_POTTERY_SHERD);

        ItemLike[] netherBiomeCatalyst = new ItemLike[]{NTItems.H_NETHER.get(), NTItems.H_CRIMSON_FOREST.get(),
            NTItems.H_WARPED_FOREST.get(), NTItems.H_SOUL_SAND_VALLEY.get(), NTItems.H_BASALT_DELTAS.get()};
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.SKELETON_SKULL).requires(NTItems.WITHER_BONE.get()).results(Items.WITHER_SKELETON_SKULL));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.COPPER_BLOCK).requires(Items.TORCH).requires(Items.REDSTONE)
            .results(Items.COPPER_BULB, 2).name(getItemName(Items.COPPER_BLOCK)));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.EXPOSED_COPPER).requires(Items.TORCH).requires(Items.REDSTONE)
            .results(Items.EXPOSED_COPPER_BULB, 2).name(getItemName(Items.EXPOSED_COPPER)));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.WEATHERED_COPPER).requires(Items.TORCH).requires(Items.REDSTONE)
            .results(Items.WEATHERED_COPPER_BULB, 2).name(getItemName(Items.EXPOSED_COPPER)));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.OXIDIZED_COPPER).requires(Items.TORCH).requires(Items.REDSTONE)
            .results(Items.OXIDIZED_COPPER_BULB, 2).name(getItemName(Items.OXIDIZED_COPPER)));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(NTItems.HARMONIOUS_CHANGE_FUEL.get()).requires(Items.LAVA_BUCKET)
            .requires(Items.BLAZE_ROD).results(NTItems.HARMONIOUS_CHANGE_LAVA_BUCKET.get()));
        harmoniousChangeWithCustomName(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.WITHER_SKELETON_SKULL).results(NTItems.WITHER_BONE.get(), 3));
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.DIAMOND).requires(Items.NETHERRACK), Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.DIAMOND).requires(Items.BLACKSTONE), Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        harmoniousChangeOfCopyIngredient(output, HarmoniousChangeRecipeBuilder.addRecipe(netherBiomeCatalyst)
            .requires(Items.DIAMOND).requires(Items.NETHER_BRICK), Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);

        harmoniousChangeOfBlockFamily(output, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE,
            NTItems.H_DESERT.get(), NTItems.H_BADLANDS.get(), NTItems.H_ICE_SPIKES.get(), NTItems.H_SNOWY_SLOPES.get());
        harmoniousChangeOfBlockFamily(output, Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS,
            NTItems.H_DESERT.get(), NTItems.H_BADLANDS.get(), NTItems.H_ICE_SPIKES.get(), NTItems.H_SNOWY_SLOPES.get());
        harmoniousChangeOfBlockFamily(output, Blocks.STONE, Blocks.MOSSY_COBBLESTONE, NTItems.H_WARM_OCEAN.get(), NTItems.H_DRIPSTONE_CAVES.get());

        AtomicInteger cobblestoneToMossyIndex = new AtomicInteger(0);
        harmoniousChangeOfBlockFamily(output, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
            NTItems.H_MEADOW.get(), NTItems.H_FOREST.get(), NTItems.H_FLOWER_FOREST.get(), NTItems.H_DARK_FOREST.get(),
            NTItems.H_BIRCH_FOREST.get(), NTItems.H_SWAMP.get(), NTItems.H_MANGROVE_SWAMP.get(), NTItems.H_JUNGLE.get(),
            NTItems.H_WARM_OCEAN.get(), NTItems.H_LUSH_CAVE.get(), NTItems.H_TAIGA.get(), NTItems.H_CHERRY_GROVE.get());
        harmoniousChangeOfBlockFamily(output, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Ingredient.of(
            NTItems.H_SAVANNA.get()), cobblestoneToMossyIndex.incrementAndGet(), Ingredient.of(Items.SHORT_GRASS));
        harmoniousChangeOfBlockFamily(output, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Ingredient.of(
            NTItems.H_RIVER.get()), cobblestoneToMossyIndex.incrementAndGet(), Ingredient.of(Items.SEAGRASS));
        harmoniousChangeOfBlockFamily(output, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
            Ingredient.of(NTItems.H_BEACH.get(), NTItems.H_STONE_SHORE.get()),
            cobblestoneToMossyIndex.incrementAndGet(), Ingredient.of(Items.KELP, Items.SEAGRASS));

        AtomicInteger stoneBrickToMossyIndex = new AtomicInteger(0);
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS,
            NTItems.H_MEADOW.get(), NTItems.H_FOREST.get(), NTItems.H_FLOWER_FOREST.get(), NTItems.H_DARK_FOREST.get(),
            NTItems.H_BIRCH_FOREST.get(), NTItems.H_SWAMP.get(), NTItems.H_MANGROVE_SWAMP.get(), NTItems.H_JUNGLE.get(),
            NTItems.H_WARM_OCEAN.get(), NTItems.H_LUSH_CAVE.get(), NTItems.H_TAIGA.get(), NTItems.H_CHERRY_GROVE.get());
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Ingredient.of(
            NTItems.H_SAVANNA.get()), stoneBrickToMossyIndex.incrementAndGet(), Ingredient.of(Items.SHORT_GRASS));
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Ingredient.of(
            NTItems.H_RIVER.get()), stoneBrickToMossyIndex.incrementAndGet(), Ingredient.of(Items.SEAGRASS));
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS,
            Ingredient.of(NTItems.H_BEACH.get(), NTItems.H_STONE_SHORE.get()),
            stoneBrickToMossyIndex.incrementAndGet(), Ingredient.of(Items.KELP, Items.SEAGRASS));

        harmoniousChangeOfBlockFamily(output, Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS, NTItems.H_WOODED_BADLANDS.get());
        harmoniousChangeOfBlockFamily(output, Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS, NTItems.H_FLOWER_FOREST.get());
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.DEEPSLATE_BRICKS, NTItems.H_DEEPSLATE.get());
        harmoniousChangeOfBlockFamily(output, Blocks.STONE_BRICKS, Blocks.TUFF_BRICKS, NTItems.H_WINDSWEPT.get());
        harmoniousChangeOfBlockFamily(output, Blocks.DEEPSLATE_BRICKS, Blocks.TUFF_BRICKS, NTItems.H_WINDSWEPT.get());
        harmoniousChangeOfBlockFamily(output, Blocks.DEEPSLATE_TILES, Blocks.TUFF_BRICKS, NTItems.H_WINDSWEPT.get());
        harmoniousChangeOfBlockFamily(output, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICKS, NTItems.H_CRIMSON_FOREST.get());
        harmoniousChangeOfBlockFamily(output, Blocks.RED_NETHER_BRICKS, Blocks.NETHER_BRICKS, NTItems.H_CRIMSON_FOREST.get());
        harmoniousChangeOfBlockFamily(output, Blocks.NETHER_BRICKS, NTBlocks.BLUE_NETHER_BRICKS.get(), NTItems.H_WARPED_FOREST.get());
        harmoniousChangeOfBlockFamily(output, NTBlocks.BLUE_NETHER_BRICKS.get(), Blocks.NETHER_BRICKS, NTItems.H_WARPED_FOREST.get());
        harmoniousChangeOfBlockFamily(output, Blocks.STONE, Blocks.END_STONE_BRICKS, NTItems.H_END_HIGHLANDS.get(), NTItems.H_END.get());
        harmoniousChangeOfBlockFamily(output, Blocks.DEEPSLATE, Blocks.END_STONE_BRICKS, NTItems.H_END_HIGHLANDS.get(), NTItems.H_END.get());
        harmoniousChangeOfBlockFamily(output, Blocks.ANDESITE, Blocks.DIORITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.DIORITE, Blocks.GRANITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.GRANITE, Blocks.ANDESITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.POLISHED_GRANITE, Blocks.POLISHED_ANDESITE, NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get());
        harmoniousChangeOfBlockFamily(output, Blocks.OAK_PLANKS, Blocks.BIRCH_PLANKS, Ingredient.of(NTItems.H_WOODED_BADLANDS.get()), 1, Ingredient.of(Items.BIRCH_SAPLING));

        harmoniousChange(output, BlockFamilies.OAK_PLANKS, NTItems.H_FOREST.get(),
            NTItems.H_FLOWER_FOREST.get(), NTItems.H_SWAMP.get(), NTItems.H_LUSH_CAVE.get());
        harmoniousChange(output, BlockFamilies.SPRUCE_PLANKS, NTItems.H_MOUNTAINS.get(), NTItems.H_TAIGA.get());
        harmoniousChange(output, BlockFamilies.DARK_OAK_PLANKS, NTItems.H_DARK_FOREST.get());
        harmoniousChange(output, BlockFamilies.BIRCH_PLANKS, NTItems.H_BIRCH_FOREST.get());
        harmoniousChange(output, BlockFamilies.JUNGLE_PLANKS, NTItems.H_JUNGLE.get());
        harmoniousChange(output, BlockFamilies.CHERRY_PLANKS, NTItems.H_CHERRY_GROVE.get());
        harmoniousChange(output, BlockFamilies.CRIMSON_PLANKS, NTItems.H_CRIMSON_FOREST.get());
        harmoniousChange(output, BlockFamilies.WARPED_PLANKS, NTItems.H_WARPED_FOREST.get());

        addCoralRecipes(output);
        addCopperWeathering(output);
        addBush(output);
        addSculkCatalyst(output);
        addInfestedBlock(output);
        addTerracottaRecovering(output);

        harmoniousChangeSpecial(output, new HCRefrigeratedRocketRecipe());
        harmoniousChangeSpecial(output, new HCMelodiousDiscRecipe());
        harmoniousChangeSpecial(output, new HCLeaderBannerRecipe());
        harmoniousChangeSpecial(output, new HCSuspiciousStewRecipe());
        harmoniousChangeSpecial(output, new HCHeroicEnchantedBookRecipe());
        waterWaxRecipes(output);
    }

    @Override
    protected @NotNull CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildRecipes(new NTRecipeOutput(this.recipePathProvider,
            this.advancementPathProvider, output, registries, set, list));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    private static ItemStack createCustomAxolotlBucket(Axolotl.Variant variant) {
        ItemStack stack = new ItemStack(Items.AXOLOTL_BUCKET);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
            tag.putInt("Variant", variant.getId());
            tag.putInt("Age", AgeableMob.BABY_START_AGE);
        });

        return stack;
    }

    public static void gathering(RecipeOutput recipeOutput, Ingredient input1, Ingredient input2, Item core, Item result, int gatheringTime) {
        GatheringRecipeBuilder.addRecipe(input1, input2, core, result, gatheringTime).unlockedBy(getHasName(core), has(core)).save(recipeOutput);
    }

    public static void gathering(RecipeOutput recipeOutput, ItemLike input1, ItemLike input2, Item core, Item result, int gatheringTime) {
        gathering(recipeOutput, Ingredient.of(input1), Ingredient.of(input2), core, result, gatheringTime);
    }

    private static void harmoniousChange(RecipeOutput recipeOutput, HarmoniousChangeRecipeBuilder builder, int index) {
        String path = BuiltInRegistries.ITEM.getKey(builder.biomeCatalyst.getItems()[0].getItem()).getPath();
        path = path.replaceFirst("h_", StringUtils.EMPTY).replaceFirst("_biome_catalyst", StringUtils.EMPTY);
        ResourceLocation id = NaturalTransmute.prefix(String.format("hc_%s_%d", path, index));
        builder.unlockedBy("has_biome_catalyst", has(NTItemTags.BIOME_CATALYST))
            .save(recipeOutput, id);
    }

    private static void harmoniousChangeWithCustomName(RecipeOutput recipeOutput, HarmoniousChangeRecipeBuilder builder) {
        StringJoiner inputJoiner = new StringJoiner("_");
        StringJoiner outputJoiner = new StringJoiner("_");
        List<ItemStack> list = Arrays.stream(builder.ingredients.getFirst().getItems()).toList();
        list.forEach(itemStack -> inputJoiner.add(getItemName(itemStack.getItem())));
        builder.results.forEach(itemStack -> outputJoiner.add(getItemName(itemStack.getItem())));
        String input = builder.name.isEmpty() ? inputJoiner.toString() : builder.name;
        ResourceLocation id = NaturalTransmute.prefix(String.format("hc_%s_to_%s", input, outputJoiner));
        builder.unlockedBy("has_biome_catalyst", has(NTItemTags.BIOME_CATALYST))
            .save(recipeOutput, id);
    }

    private static void harmoniousChangeOfCopyIngredient(RecipeOutput recipeOutput, HarmoniousChangeRecipeBuilder builder, ItemLike mainStack) {
        ResourceLocation id = NaturalTransmute.prefix(String.format("hc_copy_of_%s", getItemName(mainStack)));
        builder.requires(mainStack)
            .results(mainStack, 2)
            .unlockedBy("has_" + getItemName(mainStack), has(mainStack))
            .save(recipeOutput, id);
    }

    private static void harmoniousChange(RecipeOutput recipeOutput, ItemLike biome_catalyst, ItemLike require, ItemLike result, int index) {
        harmoniousChange(recipeOutput, HarmoniousChangeRecipeBuilder.addRecipe(biome_catalyst).requires(require).results(result), index);
    }

    private static void harmoniousChangeOfBlockFamily(RecipeOutput recipeOutput, Block oldBaseBlock, Block newBaseBlock, ItemLike... biome_catalyst) {
        HCBlockFamilyTransferRecipeBuilder builder = HCBlockFamilyTransferRecipeBuilder.addRecipe(oldBaseBlock, newBaseBlock, Ingredient.of(biome_catalyst));
        ResourceLocation id = NaturalTransmute.prefix(String.format("hc_%s_to_%s", getItemName(oldBaseBlock), getItemName(newBaseBlock)));
        builder.unlockedBy("has_biome_catalyst", has(NTItemTags.BIOME_CATALYST))
            .save(recipeOutput, id);
    }

    private static void harmoniousChangeOfBlockFamily(RecipeOutput recipeOutput, Block oldBaseBlock, Block newBaseBlock, Ingredient biome_catalyst, int index, Ingredient... extra) {
        HCBlockFamilyTransferRecipeBuilder builder = HCBlockFamilyTransferRecipeBuilder.addRecipe(oldBaseBlock, newBaseBlock, biome_catalyst);
        ResourceLocation id = NaturalTransmute.prefix(String.format("hc_%s_to_%s_with_extra_%d", getItemName(oldBaseBlock), getItemName(newBaseBlock), index));
        Arrays.stream(extra).toList().forEach(builder::requires);
        builder.unlockedBy("has_biome_catalyst", has(NTItemTags.BIOME_CATALYST))
            .save(recipeOutput, id);
    }

    private static void harmoniousChangeSpecial(RecipeOutput recipeOutput, Recipe<?> recipe) {
        ResourceLocation key = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer());
        if (key != null) {
            String s = String.format("harmonious_change/%s", key.getPath());
            ResourceLocation id = NaturalTransmute.prefix(s);
            recipeOutput.accept(id, recipe, null);
        }
    }

    private static void harmoniousChange(RecipeOutput recipeOutput, BlockFamily targetFamily, ItemLike... biome_catalyst) {
        WOOD_BLOCK_FAMILIES.stream().filter(blockFamily -> blockFamily != targetFamily).forEach(blockFamily ->
            harmoniousChangeOfBlockFamily(recipeOutput, blockFamily.getBaseBlock(), targetFamily.getBaseBlock(), biome_catalyst));
    }

    private static void waterWaxRecipes(RecipeOutput recipeOutput) {
        HoneycombItem.WAXABLES.get().forEach((b1, b2) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, b2)
            .requires(b1).requires(NTItems.WATER_WAX.get()).group(getItemName(b2))
            .unlockedBy(getHasName(b1), has(b1))
            .save(recipeOutput, NaturalTransmute.prefix("water_wax/" + getConversionRecipeName(b2, NTItems.WATER_WAX.get()))));
        WaterWax.CAN_USE_WATER_WAX.get().forEach((b1, b2) -> {
            if (!(b2 instanceof ActivatedCoralWallFan)) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, b2)
                    .requires(b1).requires(NTItems.WATER_WAX.get())
                    .group(getItemName(b2))
                    .unlockedBy(getHasName(b1), has(b1))
                    .save(recipeOutput, NaturalTransmute.prefix("water_wax/" +
                        getConversionRecipeName(b2, NTItems.WATER_WAX.get())));
            }
        });
    }

    private void buildArmorRecipes(
        RecipeOutput recipeOutput, ItemLike helmet, ItemLike chestplate,
        ItemLike leggings, ItemLike boots, ItemLike material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
            .define('X', material).pattern("XXX").pattern("X X")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
            .define('X', material).pattern("X X").pattern("XXX").pattern("XXX")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
            .define('X', material).pattern("XXX").pattern("X X").pattern("X X")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
            .define('X', material).pattern("X X").pattern("X X")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
    }

    private void buildBaseToolRecipes(
        RecipeOutput recipeOutput, ItemLike sword, ItemLike pickaxe, ItemLike shovel,
        ItemLike axe, ItemLike hoe, ItemLike material, ItemLike stick) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword)
            .define('#', stick).define('X', material)
            .pattern("X").pattern("X").pattern("#")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe)
            .define('#', stick).define('X', material)
            .pattern("XXX").pattern(" # ").pattern(" # ")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel)
            .define('#', stick).define('X', material)
            .pattern("X").pattern("#").pattern("#")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe)
            .define('#', stick).define('X', material)
            .pattern("XX").pattern("X#").pattern(" #")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe)
            .define('#', stick).define('X', material)
            .pattern("XX").pattern(" #").pattern(" #")
            .unlockedBy(getHasName(material), has(material))
            .save(recipeOutput);
    }

    @SuppressWarnings("removal")
    private record NTRecipeOutput(
        PackOutput.PathProvider recipePathProvider, PackOutput.PathProvider advancementPathProvider,
        CachedOutput output, HolderLookup.Provider registries, Set<ResourceLocation> set,
        List<CompletableFuture<?>> list) implements RecipeOutput {

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            if (this.set.add(id)) {
                this.list.add(DataProvider.saveStable(this.output, this.registries, Recipe.CONDITIONAL_CODEC,
                    Optional.of(new WithConditions<>(recipe, conditions)), this.recipePathProvider.json(id)));
                if (advancement != null) {
                    this.list.add(DataProvider.saveStable(
                        this.output, this.registries, Advancement.CONDITIONAL_CODEC,
                        Optional.of(new WithConditions<>(advancement.value(), conditions)),
                        this.advancementPathProvider.json(advancement.id())));
                }
            }
        }

        @Override
        public Advancement.@NotNull Builder advancement() {
            return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
        }

    }

    // region Categories

    private static void addCoralRecipes(RecipeOutput output) {
        coralReviving(output, Items.DEAD_TUBE_CORAL_BLOCK, Items.TUBE_CORAL_BLOCK);
        coralReviving(output, Items.DEAD_BRAIN_CORAL_BLOCK, Items.BRAIN_CORAL_BLOCK);
        coralReviving(output, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK);
        coralReviving(output, Items.DEAD_FIRE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK);
        coralReviving(output, Items.DEAD_HORN_CORAL_BLOCK, Items.HORN_CORAL_BLOCK);

        coralReviving(output, Items.DEAD_BRAIN_CORAL, Items.BRAIN_CORAL);
        coralReviving(output, Items.DEAD_BUBBLE_CORAL, Items.BUBBLE_CORAL);
        coralReviving(output, Items.DEAD_FIRE_CORAL, Items.FIRE_CORAL);
        coralReviving(output, Items.DEAD_HORN_CORAL, Items.HORN_CORAL);
        coralReviving(output, Items.DEAD_TUBE_CORAL, Items.TUBE_CORAL);

        coralReviving(output, Items.DEAD_TUBE_CORAL_FAN, Items.TUBE_CORAL_FAN);
        coralReviving(output, Items.DEAD_BRAIN_CORAL_FAN, Items.BRAIN_CORAL_FAN);
        coralReviving(output, Items.DEAD_BUBBLE_CORAL_FAN, Items.BUBBLE_CORAL_FAN);
        coralReviving(output, Items.DEAD_FIRE_CORAL_FAN, Items.FIRE_CORAL_FAN);
        coralReviving(output, Items.DEAD_HORN_CORAL_FAN, Items.HORN_CORAL_FAN);


        coralDrying(output, Items.TUBE_CORAL_BLOCK, Items.DEAD_TUBE_CORAL_BLOCK);
        coralDrying(output, Items.BRAIN_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_BLOCK);
        coralDrying(output, Items.BUBBLE_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_BLOCK);
        coralDrying(output, Items.FIRE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_BLOCK);
        coralDrying(output, Items.HORN_CORAL_BLOCK, Items.DEAD_HORN_CORAL_BLOCK);

        coralDrying(output, Items.BRAIN_CORAL, Items.DEAD_BRAIN_CORAL);
        coralDrying(output, Items.BUBBLE_CORAL, Items.DEAD_BUBBLE_CORAL);
        coralDrying(output, Items.FIRE_CORAL, Items.DEAD_FIRE_CORAL);
        coralDrying(output, Items.HORN_CORAL, Items.DEAD_HORN_CORAL);
        coralDrying(output, Items.TUBE_CORAL, Items.DEAD_TUBE_CORAL);

        coralDrying(output, Items.TUBE_CORAL_FAN, Items.DEAD_TUBE_CORAL_FAN);
        coralDrying(output, Items.BRAIN_CORAL_FAN, Items.DEAD_BRAIN_CORAL_FAN);
        coralDrying(output, Items.BUBBLE_CORAL_FAN, Items.DEAD_BUBBLE_CORAL_FAN);
        coralDrying(output, Items.FIRE_CORAL_FAN, Items.DEAD_FIRE_CORAL_FAN);
        coralDrying(output, Items.HORN_CORAL_FAN, Items.DEAD_HORN_CORAL_FAN);
    }

    private static void addCopperWeathering(RecipeOutput output) {
        copperWeathering(output, Blocks.COPPER_BLOCK);
        copperWeathering(output, Blocks.COPPER_DOOR);
        copperWeathering(output, Blocks.COPPER_TRAPDOOR);
        copperWeathering(output, Blocks.COPPER_GRATE);
        copperWeathering(output, Blocks.COPPER_BULB);

        copperWeathering(output, Blocks.CUT_COPPER);
        copperWeathering(output, Blocks.CHISELED_COPPER);
        copperWeathering(output, Blocks.CUT_COPPER_STAIRS);
        copperWeathering(output, Blocks.CUT_COPPER_SLAB);
    }

    private static void addBush(RecipeOutput output) {
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SAVANNA.get())
            .requires(NTItemTags.BUSH)
            .results(Items.WHEAT)
            .unlockedBy("has_bush", has(NTItemTags.BUSH))
            .save(output, modLoc("hc_bush_to_wheat"));
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEPSLATE.get())
            .requires(NTItemTags.BUSH)
            .results(Items.GLOW_LICHEN)
            .unlockedBy("has_bush", has(NTItemTags.BUSH))
            .save(output, modLoc("hc_bush_to_glow_lichen"));
    }

    private static void addSculkCatalyst(RecipeOutput output) {
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DEEP_DARK.get())
            .requires(NTItemTags.CORALS)
            .results(Items.SCULK_CATALYST)
            .unlockedBy("has_corals", has(NTItemTags.CORALS))
            .save(output, modLoc("hc_corals_to_sculk_catalyst"));
    }

    private static void addInfestedBlock(RecipeOutput output) {
        var infestedHost = BuiltInRegistries.BLOCK.holders()
            .filter(i -> i.is(NTBlockTags.INFESTED_HOST))
            .map(Holder.Reference::value)
            .toList();
        for (var host : infestedHost) {
            var infested = InfestedBlock.BLOCK_BY_HOST_BLOCK.get(host);
            HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get(), NTItems.H_CHERRY_GROVE.get())
                .requires(host)
                .requires(Items.SPIDER_EYE)
                .results(infested)
                .unlockedBy(getHasId(host), has(host))
                .save(output, modLoc("hc_create_infest_" + getItemId(host) + "_to_" + getItemId(infested)));
            HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_MOUNTAINS.get(), NTItems.H_SNOWY_SLOPES.get(), NTItems.H_MEADOW.get(), NTItems.H_CHERRY_GROVE.get())
                .requires(infested)
                .results(Items.GRAVEL)
                .results(NTItems.SILVERFISH_PUPA.get())
                .unlockedBy(getHasId(infested), has(infested))
                .save(output, modLoc("hc_recycle_infest_" + getItemId(infested)));
        }
    }

    private static void addTerracottaRecovering(RecipeOutput output) {
        terracottaRecovering(output, Blocks.BLACK_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.BLUE_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.BROWN_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.CYAN_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.GRAY_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.GREEN_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.LIME_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.MAGENTA_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.ORANGE_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.PINK_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.PURPLE_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.RED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.WHITE_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA);
        terracottaRecovering(output, Blocks.YELLOW_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA);
    }

    // endregion


    // region Common recipe schemas

    protected static void coralReviving(RecipeOutput output, ItemLike deadCoral, ItemLike coral) {
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WARM_OCEAN.get())
            .requires(deadCoral)
            .results(coral)
            .unlockedBy(getHasId(deadCoral), has(deadCoral))
            .save(output, modLoc("hc_revive_" + getItemId(deadCoral)));
    }

    protected static void coralDrying(RecipeOutput output, ItemLike coral, ItemLike deadCoral) {
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get(), NTItems.H_BADLANDS.get())
            .requires(coral)
            .results(deadCoral)
            .unlockedBy(getHasId(coral), has(coral))
            .save(output, modLoc("hc_drying_" + getItemId(coral)));
    }

    protected static void copperWeathering(RecipeOutput output, Block copper) {
        var current = WeatheringCopper.getFirst(copper);
        var optionalNext = WeatheringCopper.getNext(current);
        while (optionalNext.isPresent()) {
            var next = optionalNext.get();

            HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_SWAMP.get(), NTItems.H_MANGROVE_SWAMP.get(),
                    NTItems.H_JUNGLE.get(), NTItems.H_RIVER.get(), NTItems.H_OCEAN.get(),
                    NTItems.H_FROZEN_OCEAN.get(), NTItems.H_BEACH.get(), NTItems.H_STONE_SHORE.get(),
                    NTItems.H_WARM_OCEAN.get(), NTItems.H_LUSH_CAVE.get(), NTItems.H_DRIPSTONE_CAVES.get())
                .requires(current)
                .results(next)
                .unlockedBy(getHasId(current), has(current))
                .save(output, modLoc("hc_copper_weathering_" + getItemId(current) + "_to_" + getItemId(next)));

            HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_WINDSWEPT.get())
                .requires(next)
                .results(current)
                .unlockedBy(getHasId(next), has(next))
                .save(output, modLoc("hc_copper_weathering_" + getItemId(next) + "_to_" + getItemId(current)));

            current = optionalNext.get();
            optionalNext = WeatheringCopper.getNext(current);
        }
    }

    protected static void terracottaRecovering(RecipeOutput output, ItemLike glazed, ItemLike terracotta) {
        HarmoniousChangeRecipeBuilder.addRecipe(NTItems.H_DESERT.get(), NTItems.H_BADLANDS.get())
            .requires(glazed)
            .results(terracotta)
            .unlockedBy(getHasId(glazed), has(glazed))
            .save(output, modLoc("hc_recovering_" + getItemId(glazed)));
    }

    // endregion


    // region Common helpers

    protected static String getHasId(ItemLike item) {
        return "has_" + getItemId(item);
    }

    protected static String getItemId(ItemLike item) {
        var id = BuiltInRegistries.ITEM.getKey(item.asItem());
        return id.getNamespace() + "_" + id.getPath();
    }

    protected static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(NaturalTransmute.MOD_ID, path);
    }

    // endregion

}