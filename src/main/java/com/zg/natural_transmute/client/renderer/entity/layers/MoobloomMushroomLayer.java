package com.zg.natural_transmute.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zg.natural_transmute.common.entities.animal.Moobloom;
import com.zg.natural_transmute.registry.NTBlocks;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class MoobloomMushroomLayer extends MushroomCowMushroomLayer<Moobloom> {

    public MoobloomMushroomLayer(RenderLayerParent<Moobloom, CowModel<Moobloom>> renderer, BlockRenderDispatcher blockRenderer) {
        super(renderer, blockRenderer);
    }

    @Override
    public void renderMushroomBlock(PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean outlineOnly, BlockState state, int packedOverlay, BakedModel model) {
        super.renderMushroomBlock(poseStack, buffer, packedLight, outlineOnly, NTBlocks.BUTTERCUP.get().defaultBlockState(), packedOverlay, model);
    }

}