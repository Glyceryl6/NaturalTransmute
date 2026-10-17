package com.zg.natural_transmute.common.data.provider;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.blocks.ActivatedCoralWallFan;
import com.zg.natural_transmute.common.data.NTBlockFamilies;
import com.zg.natural_transmute.common.data.recipes.GatheringRecipeBuilder;
import com.zg.natural_transmute.common.items.WaterWax;
import com.zg.natural_transmute.registry.NTBlocks;
import com.zg.natural_transmute.registry.NTItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.*;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class NTRecipeProvider extends RecipeProvider {

    private static final Logger LOGGER = LogUtils.getLogger();

    public NTRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        this.buildArmorRecipes(recipeOutput, NTItems.DRAGONCAST_HELMET.get(), NTItems.DRAGONCAST_CHESTPLATE.get(),
                NTItems.DRAGONCAST_LEGGINGS.get(), NTItems.DRAGONCAST_BOOTS.get(), NTItems.DRAGONCAST_STEEL_INGOT.get());
        this.buildBaseToolRecipes(recipeOutput, NTItems.SCULK_BONE_SWORD.get(), NTItems.SCULK_BONE_PICKAXE.get(), NTItems.SCULK_BONE_SHOVEL.get(),
                NTItems.SCULK_BONE_AXE.get(), NTItems.SCULK_BONE_HOE.get(), Blocks.DEEPSLATE.asItem(), NTItems.SCULK_BONE.get());
        NTBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(blockFamily ->
                generateRecipes(recipeOutput, blockFamily, FeatureFlagSet.of(FeatureFlags.VANILLA)));
        threeByThreePacker(recipeOutput, RecipeCategory.BUILDING_BLOCKS, Blocks.WARPED_WART_BLOCK, NTItems.WARPED_WART.get());
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, NTItems.AMBER.get(),
                RecipeCategory.BUILDING_BLOCKS, NTBlocks.AMBER_BLOCK.get());
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BONE_MEAL, 6)
                .requires(NTItems.WHALE_BONE.get()).group("bonemeal")
                .unlockedBy("has_whale_bone", has(NTItems.WHALE_BONE.get()))
                .save(recipeOutput, NaturalTransmute.prefix("whale_bone_to_bonemeal"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.HARMONIOUS_CHANGE_FUEL.get())
                .requires(Ingredient.of(Items.AMETHYST_SHARD, Items.LAPIS_LAZULI)).requires(ItemTags.COALS)
                .unlockedBy("has_coals", has(ItemTags.COALS)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.CORUNDUM_IRON_PLATE.get())
                .requires(Ingredient.of(NTBlocks.CORUNDUM.get(), Items.IRON_INGOT))
                .unlockedBy("has_corundum", has(NTBlocks.CORUNDUM.get())).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, NTBlocks.BLUE_NETHER_BRICKS.get())
                .requires(NTItems.WARPED_WART.get(), 2).requires(Items.NETHER_BRICK, 2)
                .unlockedBy(getHasName(NTItems.WARPED_WART.get()), has(NTItems.WARPED_WART.get()))
                .unlockedBy(getHasName(Items.NETHER_BRICK), has(Items.NETHER_BRICK)).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE)
                .requires(NTItems.FLOWERING_BLUE_TARO_VINE.get())
                .unlockedBy(getHasName(NTItems.FLOWERING_BLUE_TARO_VINE.get()),
                        has(NTItems.FLOWERING_BLUE_TARO_VINE.get()))
                .save(recipeOutput, NaturalTransmute.prefix("blue_taro_vine_to_light_blue_dye"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PUMPKIN_PIE)
                .requires(Blocks.PUMPKIN).requires(Items.SUGAR).requires(NTItems.DUCK_EGG.get())
                .unlockedBy("has_carved_pumpkin", has(Blocks.CARVED_PUMPKIN))
                .unlockedBy("has_pumpkin", has(Blocks.PUMPKIN))
                .save(recipeOutput, NaturalTransmute.prefix("duck_egg_to_pumpkin_pie"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PAPER).requires(NTBlocks.PLANTAIN_STEM.get())
                .unlockedBy(getHasName(NTBlocks.PLANTAIN_STEM.get()), has(NTBlocks.PLANTAIN_STEM.get()))
                .save(recipeOutput, NaturalTransmute.prefix("plantain_stem_to_paper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTBlocks.HARMONIOUS_CHANGE_STOVE.get())
                .define('A', Items.COPPER_INGOT).define('B', Items.AMETHYST_SHARD)
                .define('C', NTItems.CORUNDUM_IRON_PLATE.get()).define('D', Items.HOPPER)
                .define('E', NTItems.HARMONIOUS_CHANGE_CORE.get()).define('F', Items.BLAST_FURNACE)
                .pattern("ADA").pattern("BEB").pattern("CFC")
                .unlockedBy(getHasName(NTItems.HARMONIOUS_CHANGE_CORE.get()),
                        has(NTItems.HARMONIOUS_CHANGE_CORE.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 3)
                .define('#', Blocks.SUGAR_CANE).pattern("###")
                .unlockedBy("has_papyrus", has(NTItems.PAPYRUS.get()))
                .save(recipeOutput, NaturalTransmute.prefix("papyrus_to_paper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.JUNGLE_PLANKS)
                .define('#', NTItems.COCONUT_SHELL.get()).pattern("##").pattern("##")
                .unlockedBy("has_coconut_shell", has(NTItems.COCONUT_SHELL.get()))
                .save(recipeOutput, NaturalTransmute.prefix("coconut_shell_to_jungle_plank"));
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, NTItems.WHALE_BONE_BOW.get())
                .define('#', NTItems.WHALE_BONE.get()).define('X', Items.STRING)
                .pattern(" #X").pattern("# X").pattern(" #X")
                .unlockedBy("has_string", has(Items.STRING)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTItems.HARMONIOUS_CHANGE_CORE.get())
                .define('H', NTItems.HETEROGENEOUS_STONE.get()).define('I', Items.DIAMOND)
                .pattern(" H ").pattern("HIH").pattern(" H ")
                .unlockedBy(getHasName(NTItems.HETEROGENEOUS_STONE.get()),
                        has(NTItems.HETEROGENEOUS_STONE.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Blocks.TORCH, 4)
                .define('#', Items.STICK)
                .define('X', NTItems.AMBER.get())
                .pattern("X").pattern("#")
                .unlockedBy(getHasName(NTItems.AMBER.get()), has(NTItems.AMBER.get()))
                .save(recipeOutput, NaturalTransmute.prefix("torch_from_amber"));
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Blocks.CAKE)
                .define('A', Items.MILK_BUCKET)
                .define('B', Items.SUGAR)
                .define('C', Items.WHEAT)
                .define('E', NTItems.DUCK_EGG.get())
                .pattern("AAA").pattern("BEB").pattern("CCC")
                .unlockedBy(getHasName(NTItems.DUCK_EGG.get()), has(NTItems.DUCK_EGG.get()))
                .save(recipeOutput, NaturalTransmute.prefix("duck_egg_to_cake"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NTItems.DUCK.get()), RecipeCategory.FOOD,
                        NTItems.COOKED_DUCK.get(), 0.35F, 200)
                .unlockedBy(getHasName(NTItems.DUCK.get()), has(NTItems.DUCK.get())).save(recipeOutput);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NTItems.DRAGONCAST_STEEL_BILLET.get()), RecipeCategory.MISC,
                        NTItems.DRAGONCAST_STEEL_INGOT.get(), 0.35F, 200)
                .unlockedBy(getHasName(NTItems.DRAGONCAST_STEEL_BILLET.get()),
                        has(NTItems.DRAGONCAST_STEEL_BILLET.get())).save(recipeOutput);
        // 聚相台配方
        gathering(recipeOutput, Items.SAND, Items.SAND, NTItems.HETEROGENEOUS_STONE.get(), NTItems.DESERT.get(), 160);
        gathering(recipeOutput, Items.RED_SAND, Items.RED_SAND, NTItems.HETEROGENEOUS_STONE.get(), NTItems.BADLANDS.get(), 160);
        gathering(recipeOutput, Items.OAK_SAPLING, Items.OAK_SAPLING, NTItems.BADLANDS.get(), NTItems.WOODED_BADLANDS.get(), 320);
        gathering(recipeOutput, Items.SNOW_BLOCK, Items.SNOW_BLOCK, NTItems.HETEROGENEOUS_STONE.get(), NTItems.SNOWY_PLAINS.get(), 160);
        gathering(recipeOutput, Items.PACKED_ICE, Items.PACKED_ICE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.ICE_SPIKES.get(), 160);
        gathering(recipeOutput, Ingredient.of(Items.GRAVEL, Items.STONE, Items.ANDESITE, Items.GRANITE, Items.DIORITE),
                Ingredient.of(Items.EMERALD), NTItems.HETEROGENEOUS_STONE.get(), NTItems.MOUNTAINS.get(), 160);
        gathering(recipeOutput, Items.SEAGRASS, Items.GRAVEL, NTItems.OCEAN.get(), NTItems.STONE_SHORE.get(), 320);
        gathering(recipeOutput, Items.BLUE_ICE, Items.POWDER_SNOW_BUCKET, NTItems.MOUNTAINS.get(), NTItems.SNOWY_SLOPES.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.GRASS_BLOCK), Ingredient.of(ItemTags.FLOWERS),
                NTItems.MOUNTAINS.get(), NTItems.MEADOW.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.OAK_SAPLING, Items.OAK_LEAVES, Items.OAK_LOG),
                Ingredient.of(Items.OAK_SAPLING, Items.OAK_LEAVES, Items.OAK_LOG),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.FOREST.get(), 160);
        gathering(recipeOutput, Ingredient.of(ItemTags.FLOWERS), Ingredient.of(ItemTags.FLOWERS),
                NTItems.FOREST.get(), NTItems.FLOWER_FOREST.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LEAVES, Items.DARK_OAK_LOG),
                Ingredient.of(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LEAVES, Items.DARK_OAK_LOG),
                NTItems.FOREST.get(), NTItems.DARK_FOREST.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.BIRCH_SAPLING, Items.BIRCH_LEAVES, Items.BIRCH_LOG),
                Ingredient.of(Items.BIRCH_SAPLING, Items.BIRCH_LEAVES, Items.BIRCH_LOG),
                NTItems.FOREST.get(), NTItems.BIRCH_FOREST.get(), 320);
        gathering(recipeOutput, Items.MYCELIUM, Items.MYCELIUM, NTItems.HETEROGENEOUS_STONE.get(), NTItems.MUSHROOM.get(), 160);
        gathering(recipeOutput, Items.SLIME_BALL, Items.SLIME_BALL, NTItems.HETEROGENEOUS_STONE.get(), NTItems.SWAMP.get(), 160);
        gathering(recipeOutput, Ingredient.of(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LEAVES, Items.MANGROVE_LOG),
                Ingredient.of(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LEAVES, Items.MANGROVE_LOG),
                NTItems.SWAMP.get(), NTItems.MANGROVE_SWAMP.get(), 320);
        gathering(recipeOutput, Ingredient.of(ItemTags.VILLAGER_PLANTABLE_SEEDS), Ingredient.of(Items.DIRT),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.PLAINS.get(), 160);
        gathering(recipeOutput, Ingredient.of(Items.JUNGLE_SAPLING, Items.JUNGLE_LEAVES, Items.JUNGLE_LOG),
                Ingredient.of(Items.JUNGLE_SAPLING, Items.JUNGLE_LEAVES, Items.JUNGLE_LOG),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.JUNGLE.get(), 160);
        gathering(recipeOutput, Ingredient.of(Items.ACACIA_SAPLING, Items.ACACIA_LEAVES, Items.ACACIA_LOG),
                Ingredient.of(Items.ACACIA_SAPLING, Items.ACACIA_LEAVES, Items.ACACIA_LOG),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.SAVANNA.get(), 160);
        gathering(recipeOutput, Items.CLAY, Items.WATER_BUCKET, NTItems.HETEROGENEOUS_STONE.get(), NTItems.RIVER.get(), 160);
        gathering(recipeOutput, Items.KELP, Items.WATER_BUCKET, NTItems.HETEROGENEOUS_STONE.get(), NTItems.OCEAN.get(), 160);
        gathering(recipeOutput, new BlockTagIngredient(BlockTags.ICE).toVanilla(),
                new BlockTagIngredient(BlockTags.ICE).toVanilla(),
                NTItems.OCEAN.get(), NTItems.FROZEN_OCEAN.get(), 320);
        gathering(recipeOutput, Items.KELP, Items.TURTLE_SCUTE, NTItems.OCEAN.get(), NTItems.BEACH.get(), 320);
        gathering(recipeOutput, new BlockTagIngredient(BlockTags.CORAL_PLANTS).toVanilla(),
                new BlockTagIngredient(BlockTags.CORALS).toVanilla(),
                NTItems.OCEAN.get(), NTItems.WARM_OCEAN.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.CHERRY_SAPLING, Items.CHERRY_LEAVES, Items.CHERRY_LOG),
                Ingredient.of(Items.CHERRY_SAPLING, Items.CHERRY_LEAVES, Items.CHERRY_LOG),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.CHERRY_GROVE.get(), 160);
        gathering(recipeOutput, Items.DEEPSLATE, Items.DEEPSLATE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.DEEPSLATE.get(), 160);
        gathering(recipeOutput, Items.MOSS_BLOCK, Items.MOSS_BLOCK, NTItems.DEEPSLATE.get(), NTItems.LUSH_CAVES.get(), 320);
        gathering(recipeOutput, Items.DRIPSTONE_BLOCK, Items.DRIPSTONE_BLOCK, NTItems.DEEPSLATE.get(), NTItems.DRIPSTONE_CAVES.get(), 320);
        gathering(recipeOutput, Items.SCULK, Items.SCULK, NTItems.DEEPSLATE.get(), NTItems.DEEP_DARK.get(), 320);
        gathering(recipeOutput, Ingredient.of(Items.SPRUCE_SAPLING, Items.SPRUCE_LEAVES, Items.SPRUCE_LOG),
                Ingredient.of(Items.SPRUCE_SAPLING, Items.SPRUCE_LEAVES, Items.SPRUCE_LOG),
                NTItems.HETEROGENEOUS_STONE.get(), NTItems.TAIGA.get(), 160);
        gathering(recipeOutput, Items.PODZOL, Items.MOSSY_COBBLESTONE, NTItems.TAIGA.get(), NTItems.OLD_GROWTH_TAIGA.get(), 160);
        gathering(recipeOutput, Items.WIND_CHARGE, Items.WIND_CHARGE, NTItems.HETEROGENEOUS_STONE.get(), NTItems.WINDSWEPT.get(), 160);
        gathering(recipeOutput, Items.NETHERRACK, Items.NETHERRACK, NTItems.HETEROGENEOUS_STONE.get(), NTItems.NETHER.get(), 160);
        gathering(recipeOutput, Items.CRIMSON_FUNGUS, Items.CRIMSON_ROOTS, NTItems.NETHER.get(), NTItems.CRIMSON_FOREST.get(), 320);
        gathering(recipeOutput, Items.WARPED_FUNGUS, Items.WARPED_ROOTS, NTItems.NETHER.get(), NTItems.WARPED_FOREST.get(), 320);
        gathering(recipeOutput, Items.SOUL_SAND, Items.BONE, NTItems.NETHER.get(), NTItems.SOUL_SAND_VALLEY.get(), 320);
        gathering(recipeOutput, Items.BASALT, Items.MAGMA_CREAM, NTItems.NETHER.get(), NTItems.BASALT_DELTAS.get(), 320);
        gathering(recipeOutput, Items.END_STONE, Items.CHORUS_FRUIT, NTItems.HETEROGENEOUS_STONE.get(), NTItems.END_HIGHLANDS.get(), 160);
        gathering(recipeOutput, Items.DRAGON_BREATH, Items.DRAGON_BREATH, NTItems.HETEROGENEOUS_STONE.get(), NTItems.END.get(), 160);
        waterWaxRecipes(recipeOutput);
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
        final Set<ResourceLocation> set = Sets.newHashSet();
        final List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildRecipes(new NTRecipeOutput(this.recipePathProvider,
                this.advancementPathProvider, output, registries, set, list), registries);
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public static void gathering(RecipeOutput recipeOutput, Ingredient input1, Ingredient input2, Item core, Item result, int gatheringTime) {
        GatheringRecipeBuilder.addRecipe(input1, input2, core, result, gatheringTime).unlockedBy(getHasName(core), has(core)).save(recipeOutput);
    }

    public static void gathering(RecipeOutput recipeOutput, ItemLike input1, ItemLike input2, Item core, Item result, int gatheringTime) {
        gathering(recipeOutput, Ingredient.of(input1), Ingredient.of(input2), core, result, gatheringTime);
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
            if (!this.set.add(id)) {
                LOGGER.warn("Duplicate recipe {} is ignored", id);
            } else {
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

}
