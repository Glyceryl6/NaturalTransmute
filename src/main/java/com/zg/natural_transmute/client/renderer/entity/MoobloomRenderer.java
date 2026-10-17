package com.zg.natural_transmute.client.renderer.entity;

import com.zg.natural_transmute.NaturalTransmute;
import com.zg.natural_transmute.client.renderer.entity.layers.MoobloomMushroomLayer;
import com.zg.natural_transmute.common.entities.animal.Moobloom;
import com.zg.natural_transmute.registry.NTModelLayers;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MoobloomRenderer extends MobRenderer<Moobloom, CowModel<Moobloom>> {

    public MoobloomRenderer(EntityRendererProvider.Context context) {
        super(context, new CowModel<>(context.bakeLayer(NTModelLayers.MOOBLOOM)), 0.7F);
        this.addLayer(new MoobloomMushroomLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(Moobloom entity) {
        return NaturalTransmute.prefix("textures/entity/moobloom.png");
    }

}