package com.lazrproductions.cuffed.entity.renderer;

import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.model.ChainKnotEntityModel;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChainKnotEntityRenderer extends EntityRenderer<ChainKnotEntity> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/entity/chain_knot.png");
   private final ChainKnotEntityModel<ChainKnotEntity> model;
   public static PoseStack POSESTACK;
   public static MultiBufferSource BUFFER;

   public ChainKnotEntityRenderer(Context context) {
      super(context);
      this.model = new ChainKnotEntityModel(context.bakeLayer(ModModelLayers.CHAIN_KNOT_LAYER));
   }

   public void render(@Nonnull ChainKnotEntity entity, float yaw, float partialTicks, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light) {
      super.render(entity, yaw, partialTicks, stack, buffer, light);
      POSESTACK = stack;
      BUFFER = buffer;
      this.model.setOnFence(!entity.isOnFence());
      stack.pushPose();
      this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
      this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, -1);
      stack.popPose();
   }

   public ResourceLocation getTextureLocation(@Nonnull ChainKnotEntity entity) {
      return TEXTURE_LOCATION;
   }
}
