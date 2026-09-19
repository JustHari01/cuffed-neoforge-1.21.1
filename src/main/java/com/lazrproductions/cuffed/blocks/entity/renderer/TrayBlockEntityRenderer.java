package com.lazrproductions.cuffed.blocks.entity.renderer;

import com.lazrproductions.cuffed.blocks.TrayBlock;
import com.lazrproductions.cuffed.blocks.entity.TrayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class TrayBlockEntityRenderer implements BlockEntityRenderer<TrayBlockEntity> {
   public TrayBlockEntityRenderer(Context ctx) {
   }

   public void render(@Nonnull TrayBlockEntity entity, float partialTick, @Nonnull PoseStack stack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
      ItemStack foodStack = entity.getFoodStack();
      if (!foodStack.isEmpty()) {
         stack.pushPose();
         Direction facing = (Direction)entity.getBlockState().getValue(TrayBlock.FACING);
         switch (facing) {
            case NORTH:
               stack.translate(0.66F, 0.05F, 0.52F);
               break;
            case SOUTH:
               stack.translate(0.34F, 0.05F, 0.48F);
               break;
            case WEST:
               stack.translate(0.52F, 0.05F, 0.34F);
               break;
            default:
               stack.translate(0.48F, 0.05F, 0.66F);
         }

         stack.scale(0.35F, 0.35F, 0.35F);
         stack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot()));
         stack.mulPose(Axis.XP.rotationDegrees(90.0F));
         itemRenderer.renderStatic(
            foodStack,
            ItemDisplayContext.FIXED,
            this.getLightLevel(entity.getLevel(), entity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            stack,
            buffer,
            entity.getLevel(),
            1
         );
         stack.popPose();
      }

      ItemStack forkStack = entity.getForkStack();
      if (!forkStack.isEmpty()) {
         stack.pushPose();
         Direction facing = (Direction)entity.getBlockState().getValue(TrayBlock.FACING);
         switch (facing) {
            case NORTH:
               stack.translate(0.44F, 0.05F, 0.52F);
               break;
            case SOUTH:
               stack.translate(0.56F, 0.05F, 0.48F);
               break;
            case WEST:
               stack.translate(0.52F, 0.05F, 0.56F);
               break;
            default:
               stack.translate(0.48F, 0.05F, 0.44F);
         }

         stack.scale(0.28F, 0.28F, 0.28F);
         stack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot() - 45.0F));
         stack.mulPose(Axis.XP.rotationDegrees(90.0F));
         itemRenderer.renderStatic(
            forkStack,
            ItemDisplayContext.FIXED,
            this.getLightLevel(entity.getLevel(), entity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            stack,
            buffer,
            entity.getLevel(),
            1
         );
         stack.popPose();
      }

      ItemStack spoonStack = entity.getSpoonStack();
      if (!spoonStack.isEmpty()) {
         stack.pushPose();
         Direction facing = (Direction)entity.getBlockState().getValue(TrayBlock.FACING);
         switch (facing) {
            case NORTH:
               stack.translate(0.365F, 0.05F, 0.52F);
               break;
            case SOUTH:
               stack.translate(0.635F, 0.05F, 0.48F);
               break;
            case WEST:
               stack.translate(0.52F, 0.05F, 0.635F);
               break;
            default:
               stack.translate(0.48F, 0.05F, 0.365F);
         }

         stack.scale(0.28F, 0.28F, 0.28F);
         stack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot() - 45.0F));
         stack.mulPose(Axis.XP.rotationDegrees(90.0F));
         itemRenderer.renderStatic(
            spoonStack,
            ItemDisplayContext.FIXED,
            this.getLightLevel(entity.getLevel(), entity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            stack,
            buffer,
            entity.getLevel(),
            1
         );
         stack.popPose();
      }

      ItemStack knifeStack = entity.getKnifeStack();
      if (!knifeStack.isEmpty()) {
         stack.pushPose();
         Direction facing = (Direction)entity.getBlockState().getValue(TrayBlock.FACING);
         switch (facing) {
            case NORTH:
               stack.translate(0.27F, 0.05F, 0.52F);
               break;
            case SOUTH:
               stack.translate(0.73F, 0.05F, 0.48F);
               break;
            case WEST:
               stack.translate(0.52F, 0.05F, 0.73F);
               break;
            default:
               stack.translate(0.48F, 0.05F, 0.27F);
         }

         stack.scale(0.28F, 0.28F, 0.28F);
         stack.mulPose(Axis.YN.rotationDegrees(facing.getOpposite().toYRot() - 45.0F));
         stack.mulPose(Axis.XP.rotationDegrees(90.0F));
         itemRenderer.renderStatic(
            knifeStack,
            ItemDisplayContext.FIXED,
            this.getLightLevel(entity.getLevel(), entity.getBlockPos()),
            OverlayTexture.NO_OVERLAY,
            stack,
            buffer,
            entity.getLevel(),
            1
         );
         stack.popPose();
      }
   }

   private int getLightLevel(Level level, BlockPos pos) {
      int bLight = level.getBrightness(LightLayer.BLOCK, pos);
      int sLight = level.getBrightness(LightLayer.SKY, pos);
      return LightTexture.pack(bLight, sLight);
   }
}
