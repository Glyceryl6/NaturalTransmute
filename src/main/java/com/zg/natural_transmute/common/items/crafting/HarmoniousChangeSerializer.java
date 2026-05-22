package com.zg.natural_transmute.common.items.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;

public class HarmoniousChangeSerializer implements RecipeSerializer<HarmoniousChangeRecipe> {

    private static final MapCodec<HarmoniousChangeRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SizedIngredient.FLAT_CODEC.listOf(1, 3).fieldOf("ingredients").forGetter(HarmoniousChangeRecipe::getIngredients),
                    Ingredient.CODEC.listOf().fieldOf("excepts").forGetter(HarmoniousChangeRecipe::getExcepts),
                    ItemStack.STRICT_CODEC.listOf(1, 3).fieldOf("results").forGetter(HarmoniousChangeRecipe::getResults),
                    Ingredient.CODEC_NONEMPTY.fieldOf("biome_catalysts").forGetter(HarmoniousChangeRecipe::getBiomeCatalysts),
                    Codec.INT.fieldOf("time").forGetter(HarmoniousChangeRecipe::getTime),
                    Codec.BOOL.fieldOf("consume").forGetter(HarmoniousChangeRecipe::isConsume)
            ).apply(instance, HarmoniousChangeRecipe::new));

    @Override
    public MapCodec<HarmoniousChangeRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, HarmoniousChangeRecipe> streamCodec() {
        return StreamCodec.of(this::toNetwork, this::fromNetwork);
    }

    private HarmoniousChangeRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        int ingredientsSize = buffer.readVarInt();
        int exceptsSize = buffer.readVarInt();
        int resultsSize = buffer.readVarInt();

        var ingredients = new ArrayList<SizedIngredient>();
        var excepts = new ArrayList<Ingredient>();
        var results = new ArrayList<ItemStack>();

        for (var i = 0; i < ingredientsSize; i++) {
            var decoded = SizedIngredient.STREAM_CODEC.decode(buffer);
            ingredients.add(decoded);
        }

        for (var i = 0; i < exceptsSize; i++) {
            var decoded = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            excepts.add(decoded);
        }

        for (var i = 0; i < resultsSize; i++) {
            var decoded = ItemStack.STREAM_CODEC.decode(buffer);
            results.add(decoded);
        }

        Ingredient biome_catalyst = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        if (biome_catalyst.isEmpty()) {
            biome_catalyst = Ingredient.EMPTY;
        }

        return new HarmoniousChangeRecipe(ingredients, excepts, results,
                biome_catalyst, buffer.readVarInt(), buffer.readBoolean());
    }


    private void toNetwork(RegistryFriendlyByteBuf buffer, HarmoniousChangeRecipe recipe) {
        buffer.writeVarInt(recipe.getIngredients().size());
        buffer.writeVarInt(recipe.getExcepts().size());
        buffer.writeVarInt(recipe.getResults().size());
        for (SizedIngredient ingredient : recipe.getIngredients()) {
            SizedIngredient.STREAM_CODEC.encode(buffer, ingredient);
        }

        for (Ingredient ingredient : recipe.getExcepts()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }

        for (ItemStack stack : recipe.getResults()) {
            ItemStack.STREAM_CODEC.encode(buffer, stack);
        }

        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getBiomeCatalysts());
        buffer.writeVarInt(recipe.getTime());
        buffer.writeBoolean(recipe.isConsume());
    }

}