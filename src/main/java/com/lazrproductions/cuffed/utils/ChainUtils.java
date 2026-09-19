package com.lazrproductions.cuffed.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ChainUtils {
   public static void renderChainTo(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom) {
      if (entityFrom != null) {
         float maxLength = 12.0F;
         float distance = entity.distanceTo(entityFrom) / maxLength;
         if (distance > maxLength) {
            distance = maxLength;
         }

         renderVerticalTo(entity, partialTicks, poseStack, bufferSource, entityFrom, distance);
         renderHorizontalTo(entity, partialTicks, poseStack, bufferSource, entityFrom, distance);
      }
   }

   public static void renderChainFrom(Entity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo) {
      if (entityTo != null) {
         float maxLength = 12.0F;
         float distance = entity.distanceTo(entityTo) / maxLength;
         if (distance > maxLength) {
            distance = maxLength;
         }

         renderVerticalFrom(entity, partialTicks, poseStack, bufferSource, entityTo, distance);
         renderHorizontalFrom(entity, partialTicks, poseStack, bufferSource, entityTo, distance);
      }
   }

   public static void renderVerticalFrom(
      Entity entityTo, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom, float distance
   ) {
      if (entityTo != null) {
         poseStack.pushPose();
         Vec3 startPos = entityTo.getPosition(partialTicks).add(new Vec3(0.0, 1.4, 0.0));
         double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
         Vec3 endPosOffset = entityFrom.getRopeHoldPosition(partialTicks).subtract(entityFrom.position());
         double d1 = org.joml.Math.cos(d0) * endPosOffset.z + org.joml.Math.sin(d0) * endPosOffset.x;
         double d2 = org.joml.Math.sin(d0) * endPosOffset.z - org.joml.Math.cos(d0) * endPosOffset.x;
         double ePosX = Mth.lerp((double)partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
         double ePosY = Mth.lerp((double)partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
         double ePosZ = Mth.lerp((double)partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;
         poseStack.translate(d1, endPosOffset.y, d2);
         float f = (float)(startPos.x - ePosX);
         float f1 = (float)(startPos.y - ePosY);
         float f2 = (float)(startPos.z - ePosZ);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
         Matrix4f pose = poseStack.last().pose();
         float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
         float f5 = f2 * f4;
         float f6 = f * f4;
         Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
         Vec3 _hEye = entityTo.getEyePosition(partialTicks);
         BlockPos mobEyePosition = new BlockPos((int)org.joml.Math.floor(_mEye.x), (int)org.joml.Math.floor(_mEye.y), (int)org.joml.Math.floor(_mEye.z));
         BlockPos holderEyePos = new BlockPos((int)org.joml.Math.floor(_hEye.x), (int)org.joml.Math.floor(_hEye.y), (int)org.joml.Math.floor(_hEye.z));
         int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
         int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
         int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
         int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

         for (int i = 0; i <= 24; i++) {
            addChainLink(
               vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.025F, f5, f6, i, false, distance
            );
         }

         poseStack.popPose();
      }
   }

   public static void renderHorizontalFrom(
      Entity entityTo, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityFrom, float distance
   ) {
      if (entityTo != null) {
         poseStack.pushPose();
         Vec3 startPos = entityTo.getPosition(partialTicks).add(new Vec3(0.0, 1.4, 0.0));
         double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
         Vec3 endPosOffset = entityFrom.getRopeHoldPosition(partialTicks).subtract(entityFrom.position());
         double d1 = org.joml.Math.cos(d0) * endPosOffset.z + org.joml.Math.sin(d0) * endPosOffset.x;
         double d2 = org.joml.Math.sin(d0) * endPosOffset.z - org.joml.Math.cos(d0) * endPosOffset.x;
         double ePosX = Mth.lerp((double)partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
         double ePosY = Mth.lerp((double)partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
         double ePosZ = Mth.lerp((double)partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;
         poseStack.translate(d1, endPosOffset.y, d2);
         float x = (float)(startPos.x - ePosX);
         float y = (float)(startPos.y - ePosY);
         float z = (float)(startPos.z - ePosZ);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
         Matrix4f pose = poseStack.last().pose();
         float f4 = Mth.invSqrt(x * x + z * z) * 0.025F / 2.0F;
         float f5 = z * f4;
         float f6 = x * f4;
         Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
         Vec3 _hEye = entityTo.getEyePosition(partialTicks);
         BlockPos mobEyePosition = new BlockPos((int)org.joml.Math.floor(_mEye.x), (int)org.joml.Math.floor(_mEye.y), (int)org.joml.Math.floor(_mEye.z));
         BlockPos holderEyePos = new BlockPos((int)org.joml.Math.floor(_hEye.x), (int)org.joml.Math.floor(_hEye.y), (int)org.joml.Math.floor(_hEye.z));
         int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
         int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
         int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
         int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

         for (int i = 0; i <= 24; i++) {
            addChainLink(
               vertexConsumer, pose, x, y, z, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.0F, f5, f6, i, true, distance
            );
         }

         poseStack.popPose();
      }
   }

   public static void renderVerticalTo(
      Entity entityFrom, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo, float distance
   ) {
      if (entityTo != null) {
         poseStack.pushPose();
         Vec3 startPos = entityTo.getRopeHoldPosition(partialTicks);
         double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
         Vec3 endPosOffset = entityFrom.getLeashOffset(partialTicks);
         double d1 = org.joml.Math.cos(d0) * endPosOffset.z + org.joml.Math.sin(d0) * endPosOffset.x;
         double d2 = org.joml.Math.sin(d0) * endPosOffset.z - org.joml.Math.cos(d0) * endPosOffset.x;
         double ePosX = Mth.lerp((double)partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
         double ePosY = Mth.lerp((double)partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
         double ePosZ = Mth.lerp((double)partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;
         poseStack.translate(d1, endPosOffset.y, d2);
         float f = (float)(startPos.x - ePosX);
         float f1 = (float)(startPos.y - ePosY);
         float f2 = (float)(startPos.z - ePosZ);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
         Matrix4f pose = poseStack.last().pose();
         float f4 = Mth.invSqrt(f * f + f2 * f2) * 0.025F / 2.0F;
         float f5 = f2 * f4;
         float f6 = f * f4;
         Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
         Vec3 _hEye = entityTo.getEyePosition(partialTicks);
         BlockPos mobEyePosition = new BlockPos((int)org.joml.Math.floor(_mEye.x), (int)org.joml.Math.floor(_mEye.y), (int)org.joml.Math.floor(_mEye.z));
         BlockPos holderEyePos = new BlockPos((int)org.joml.Math.floor(_hEye.x), (int)org.joml.Math.floor(_hEye.y), (int)org.joml.Math.floor(_hEye.z));
         int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
         int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
         int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
         int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

         for (int i = 0; i <= 24; i++) {
            addChainLink(
               vertexConsumer, pose, f, f1, f2, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.025F, f5, f6, i, false, distance
            );
         }

         poseStack.popPose();
      }
   }

   public static void renderHorizontalTo(
      Entity entityFrom, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, Entity entityTo, float distance
   ) {
      if (entityTo != null) {
         poseStack.pushPose();
         Vec3 startPos = entityTo.getRopeHoldPosition(partialTicks);
         double d0 = (double)(Mth.lerp(partialTicks, entityFrom.yRotO, entityFrom.yRotO) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
         Vec3 endPosOffset = entityFrom.getLeashOffset(partialTicks);
         double d1 = org.joml.Math.cos(d0) * endPosOffset.z + org.joml.Math.sin(d0) * endPosOffset.x;
         double d2 = org.joml.Math.sin(d0) * endPosOffset.z - org.joml.Math.cos(d0) * endPosOffset.x;
         double ePosX = Mth.lerp((double)partialTicks, entityFrom.xo, entityFrom.getX()) + d1;
         double ePosY = Mth.lerp((double)partialTicks, entityFrom.yo, entityFrom.getY()) + endPosOffset.y;
         double ePosZ = Mth.lerp((double)partialTicks, entityFrom.zo, entityFrom.getZ()) + d2;
         poseStack.translate(d1, endPosOffset.y, d2);
         float x = (float)(startPos.x - ePosX);
         float y = (float)(startPos.y - ePosY);
         float z = (float)(startPos.z - ePosZ);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
         Matrix4f pose = poseStack.last().pose();
         float f4 = Mth.invSqrt(x * x + z * z) * 0.025F / 2.0F;
         float f5 = z * f4;
         float f6 = x * f4;
         Vec3 _mEye = entityFrom.getEyePosition(partialTicks);
         Vec3 _hEye = entityTo.getEyePosition(partialTicks);
         BlockPos mobEyePosition = new BlockPos((int)org.joml.Math.floor(_mEye.x), (int)org.joml.Math.floor(_mEye.y), (int)org.joml.Math.floor(_mEye.z));
         BlockPos holderEyePos = new BlockPos((int)org.joml.Math.floor(_hEye.x), (int)org.joml.Math.floor(_hEye.y), (int)org.joml.Math.floor(_hEye.z));
         int mobLightLevel = getBlockLightLevel(entityFrom, mobEyePosition);
         int holderLightLevel = getBlockLightLevel(entityTo, holderEyePos);
         int mobBrightness = entityFrom.level().getBrightness(LightLayer.SKY, mobEyePosition);
         int holderBrightness = entityFrom.level().getBrightness(LightLayer.SKY, holderEyePos);

         for (int i = 0; i <= 24; i++) {
            addChainLink(
               vertexConsumer, pose, x, y, z, mobLightLevel, holderLightLevel, mobBrightness, holderBrightness, 0.025F, 0.0F, f5, f6, i, true, distance
            );
         }

         poseStack.popPose();
      }
   }

   protected static int getBlockLightLevel(Entity mob, BlockPos pos) {
      return mob.isOnFire() ? 15 : mob.level().getBrightness(LightLayer.BLOCK, pos);
   }

   private static void addChainLink(
      VertexConsumer vertexConsumer,
      Matrix4f pose,
      float _x,
      float _y,
      float _z,
      int mobLightLevel,
      int holderLightLevel,
      int mobBrightness,
      int holderBrightness,
      float endHeight,
      float startHeight,
      float xWidth,
      float zWidth,
      int index,
      boolean alternate,
      float distance
   ) {
      float leashProgress = (float)index / 24.0F;
      int segmentLight = (int)Mth.lerp(leashProgress, (float)mobLightLevel, (float)holderLightLevel);
      int segmentBrightness = (int)Mth.lerp(leashProgress, (float)mobBrightness, (float)holderBrightness);
      int lightmap = LightTexture.pack(segmentLight, segmentBrightness);
      float[] rr = new float[]{0.28627452F, 0.24313726F, 0.14509805F};
      float[] rg = new float[]{0.3137255F, 0.26666668F, 0.17254902F};
      float[] rb = new float[]{0.39607844F, 0.3254902F, 0.23921569F};
      Random random = new Random((long)(index * 1000));
      int colorI = org.joml.Math.round(random.nextFloat() * 2.49F);
      float r = rr[colorI];
      float g = rg[colorI];
      float b = rb[colorI];
      float vertX = _x * leashProgress;
      float vertY = (_y > 0.0F ? _y * leashProgress * leashProgress : _y - _y * (1.0F - leashProgress) * (1.0F - leashProgress))
         - (float)org.joml.Math.sin((double)leashProgress * Math.PI) * 0.6F * -(distance - 1.0F);
      float vertZ = _z * leashProgress;
      float nextLeashProgress = (float)(index + 1) / 24.0F;
      float vertX1 = _x * nextLeashProgress;
      float vertY1 = (_y > 0.0F ? _y * nextLeashProgress * nextLeashProgress : _y - _y * (1.0F - nextLeashProgress) * (1.0F - nextLeashProgress))
         - (float)org.joml.Math.sin((double)leashProgress * Math.PI) * 0.6F * -(distance - 1.0F);
      float vertZ1 = _z * nextLeashProgress;
      float xWidth1 = xWidth * 2.0F;
      float zWidth1 = zWidth * 2.0F;
      float height = startHeight * 2.0F;
      float eHeight = endHeight * 2.0F;
      vertexConsumer.addVertex(pose, vertX - xWidth1, vertY + height, vertZ + zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
      if (index % 2 == (alternate ? 1 : 0)) {
         vertexConsumer.addVertex(pose, vertX - xWidth1, vertY + height, vertZ + zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
         vertexConsumer.addVertex(pose, vertX + xWidth1, vertY + eHeight - height, vertZ - zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
         vertexConsumer.addVertex(pose, vertX1 - xWidth1, vertY1 + height, vertZ1 + zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
         vertexConsumer.addVertex(pose, vertX1 + xWidth1, vertY1 + eHeight - height, vertZ1 - zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
      } else {
         vertexConsumer.addVertex(pose, vertX - xWidth1, vertY + height, vertZ + zWidth1).setColor(r, g, b, 0.0F).setLight(lightmap);
      }
   }
}
