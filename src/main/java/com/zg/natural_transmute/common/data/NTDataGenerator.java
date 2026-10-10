package com.zg.natural_transmute.common.data;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.common.data.models.NTModelProvider;
import com.zg.natural_transmute.common.data.provider.*;
import com.zg.natural_transmute.common.data.provider.tag.NTBlockTagsProvider;
import com.zg.natural_transmute.common.data.provider.tag.NTEntityTagsProvider;
import com.zg.natural_transmute.common.data.provider.tag.NTItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = NaturalTransmute.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NTDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        NTBlockTagsProvider blockTagsProvider = new NTBlockTagsProvider(output, provider, existingFileHelper);
        DatapackBuiltinEntriesProvider dataPackProvider = new RegistryDataGenerator(output, provider);
        CompletableFuture<HolderLookup.Provider> registryProvider = dataPackProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new NTItemTagProvider(
                output, provider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), dataPackProvider);
        generator.addProvider(event.includeServer(), NTAdvancementProvider.create(output, provider));
        generator.addProvider(event.includeServer(), new NTEntityTagsProvider(output, provider, existingFileHelper));
        generator.addProvider(event.includeServer(), new NTBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new NTItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new NTLanguageProvider(output, "en_us"));
        generator.addProvider(event.includeServer(), new NTLanguageProvider(output, "zh_cn"));
        generator.addProvider(event.includeServer(), new NTLootTableProvider(output, provider));
        generator.addProvider(event.includeServer(), new NTDataMapProvider(output, provider));
        generator.addProvider(event.includeServer(), new NTRecipeProvider(output, registryProvider));
        generator.getVanillaPack(event.includeServer()).addProvider(NTModelProvider::new);
    }

}