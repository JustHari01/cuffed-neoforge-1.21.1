package com.lazrproductions.cuffed.blocks.entity.renderer;

import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.model.GuillotineBlockEntityModel;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class GuillotineBlockEntityRenderer implements BlockEntityRenderer<GuillotineBlockEntity> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/block/guillotine.png");
   GuillotineBlockEntityModel<GuillotineBlockEntity> model;

   public GuillotineBlockEntityRenderer(Context ctx) {
      this.model = new GuillotineBlockEntityModel<>(ctx.bakeLayer(ModModelLayers.GUILLOTINE_LAYER));
   }

   public void render(
      @Nonnull GuillotineBlockEntity entity, float partialTick, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, int overlay
   ) {
      this.model.setupAnim(entity, partialTick);
      this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
   }
}
