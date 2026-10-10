package com.zg.natural_transmute.common.items.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HarmoniousChangeSerializer<T extends HarmoniousChangeRecipe> implements RecipeSerializer<T> {

    private static final Codec<List<Optional<SizedIngredient>>> INPUTS_CODEC = slotsCodec(
        ExtraCodecs.optionalEmptyMap(SizedIngredient.FLAT_CODEC),
        HarmoniousChangeRecipe.INPUT_SLOTS, Optional.empty(), Optional::isEmpty);
    private static final Codec<List<ItemStack>> OUTPUTS_CODEC = slotsCodec(
        ExtraCodecs.optionalEmptyMap(ItemStack.STRICT_CODEC).xmap(
            stack -> stack.orElse(ItemStack.EMPTY),
            stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)),
        HarmoniousChangeRecipe.OUTPUT_SLOTS, ItemStack.EMPTY, ItemStack::isEmpty);
    private static final StreamCodec<RegistryFriendlyByteBuf, List<Optional<SizedIngredient>>> INPUTS_STREAM_CODEC =
        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).apply(ByteBufCodecs.list(HarmoniousChangeRecipe.INPUT_SLOTS));
    private static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> OUTPUTS_STREAM_CODEC =
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(HarmoniousChangeRecipe.OUTPUT_SLOTS));

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public HarmoniousChangeSerializer(Factory<T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            INPUTS_CODEC.fieldOf("inputs").forGetter(HarmoniousChangeRecipe::getInputs),
            Ingredient.CODEC_NONEMPTY.fieldOf("biome_catalyst").forGetter(HarmoniousChangeRecipe::getBiomeCatalyst),
            OUTPUTS_CODEC.fieldOf("outputs").forGetter(HarmoniousChangeRecipe::getOutputs),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "time",
                HarmoniousChangeRecipe.DEFAULT_TIME).forGetter(HarmoniousChangeRecipe::getTime),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(Codec.BOOL, "consume",
                Boolean.TRUE).forGetter(HarmoniousChangeRecipe::isConsume)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.composite(
            INPUTS_STREAM_CODEC, HarmoniousChangeRecipe::getInputs,
            Ingredient.CONTENTS_STREAM_CODEC, HarmoniousChangeRecipe::getBiomeCatalyst,
            OUTPUTS_STREAM_CODEC, HarmoniousChangeRecipe::getOutputs,
            ByteBufCodecs.VAR_INT, HarmoniousChangeRecipe::getTime,
            ByteBufCodecs.BOOL, HarmoniousChangeRecipe::isConsume,
            factory::create);
    }

    /**
     * Ordered slots serialized as a list in which {@code {}} marks an empty slot.
     * Trailing empty slots are omitted when encoding and padded back when decoding.
     */
    private static <E> Codec<List<E>> slotsCodec(Codec<E> slotCodec, int size, E empty, Predicate<E> isEmpty) {
        return slotCodec.listOf(1, size).validate(slots -> slots.stream().allMatch(isEmpty)
            ? DataResult.error(() -> "At least one slot must not be empty")
            : DataResult.success(slots)).xmap(slots -> {
            List<E> padded = new ArrayList<>(slots);
            while (padded.size() < size) {
                padded.add(empty);
            }

            return List.copyOf(padded);
        }, slots -> {
            int end = slots.size();
            while (end > 0 && isEmpty.test(slots.get(end - 1))) {
                end--;
            }

            return slots.subList(0, end);
        });
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    public interface Factory<T extends HarmoniousChangeRecipe> {
        T create(List<Optional<SizedIngredient>> inputs, Ingredient biomeCatalyst,
                 List<ItemStack> outputs, int time, boolean consume);

    }
}
