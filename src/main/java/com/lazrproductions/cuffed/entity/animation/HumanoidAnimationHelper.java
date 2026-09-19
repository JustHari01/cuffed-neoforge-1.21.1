package com.lazrproductions.cuffed.entity.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidAnimationHelper {
   public static void animateArmsTiedBack(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1, float f2, float f3, float headYRot, float headXRot) {
      animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);
      ModelPart head = model.head;
      ModelPart body = model.body;
      ModelPart leftLeg = model.leftLeg;
      ModelPart rightLeg = model.rightLeg;
      ModelPart leftArm = model.leftArm;
      ModelPart rightArm = model.rightArm;
      if (!entity.isCrouching()) {
         rightArm.yRot = (float) (Math.PI * 2.0 / 9.0);
         rightArm.xRot = (float) (Math.PI * 2.0 / 9.0);
         leftArm.yRot = (float) (-Math.PI * 2.0 / 9.0);
         leftArm.xRot = (float) (Math.PI * 2.0 / 9.0);
      } else {
         rightArm.yRot = (float) (Math.PI / 9);
         rightArm.xRot = 1.2740903F;
         leftArm.yRot = (float) (-Math.PI / 9);
         leftArm.xRot = 1.2740903F;
      }

      model.hat.copyFrom(head);
   }

   public static void animateArmsTiedFront(LivingEntity entity, HumanoidModel<LivingEntity> model, float f1, float f2, float f3, float headYRot, float headXRot) {
      animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);
      ModelPart head = model.head;
      ModelPart body = model.body;
      ModelPart leftLeg = model.leftLeg;
      ModelPart rightLeg = model.rightLeg;
      ModelPart leftArm = model.leftArm;
      ModelPart rightArm = model.rightArm;
      if (!entity.isCrouching()) {
         rightArm.yRot = (float) (-Math.PI / 6);
         rightArm.xRot = (float) (-Math.PI * 2.0 / 9.0);
         leftArm.yRot = (float) (Math.PI / 6);
         leftArm.xRot = (float) (-Math.PI * 2.0 / 9.0);
      } else {
         rightArm.yRot = (float) (-Math.PI / 2);
         rightArm.xRot = -0.29670596F;
         leftArm.yRot = (float) (Math.PI / 2);
         leftArm.xRot = -0.29670596F;
      }

      model.hat.copyFrom(head);
   }

   public static void animatePilloryDetainedAnimation(
      LivingEntity entity, HumanoidModel<LivingEntity> model, float f1, float f2, float f3, float headYRot, float headXRot, float forwardRotation
   ) {
      entity.setYBodyRot(forwardRotation);
      entity.setYHeadRot(forwardRotation);
      entity.setYRot(forwardRotation);
      animateDefaultHumanoid(entity, model, f1, f2, f3, headYRot, headXRot);
      ModelPart head = model.head;
      ModelPart body = model.body;
      ModelPart leftLeg = model.leftLeg;
      ModelPart rightLeg = model.rightLeg;
      ModelPart leftArm = model.leftArm;
      ModelPart rightArm = model.rightArm;
      rightLeg.z = -2.0F;
      rightLeg.y = 14.2F;
      rightLeg.xRot = (float) (Math.PI / 18);
      rightLeg.yRot = 0.08726646F;
      rightLeg.zRot = (float) (Math.PI / 18);
      leftLeg.y = 16.2F;
      leftLeg.xRot = (float) (Math.PI * 2.0 / 9.0);
      leftLeg.yRot = 0.08726646F;
      leftLeg.zRot = (float) (-Math.PI / 18);
      head.z = -7.0F;
      head.y = 9.6F;
      head.xRot = (float) (Math.PI * 2.0 / 9.0);
      body.y = 7.5F;
      body.z = -5.2F;
      body.xRot = 0.87266463F;
      leftArm.y = 8.3F;
      leftArm.z = -3.0F;
      leftArm.xRot = -1.2217305F;
      leftArm.zRot = (float) (-Math.PI / 18);
      rightArm.y = 8.3F;
      rightArm.z = -3.0F;
      rightArm.xRot = -1.2217305F;
      rightArm.zRot = (float) (Math.PI / 18);
      head.yRot = 0.0F;
      model.hat.copyFrom(head);
   }

   public static void animateDefaultHumanoid(
      LivingEntity entity, HumanoidModel<LivingEntity> model, float f1, float f2, float f3, float headYRot, float headXRot
   ) {
      ModelPart head = model.head;
      ModelPart body = model.body;
      ModelPart leftLeg = model.leftLeg;
      ModelPart rightLeg = model.rightLeg;
      ModelPart leftArm = model.leftArm;
      ModelPart rightArm = model.rightArm;
      head.z = 0.0F;
      body.z = 0.0F;
      boolean flag = entity.getFallFlyingTicks() > 4;
      head.yRot = headYRot * (float) (Math.PI / 180.0);
      head.xRot = headXRot * (float) (Math.PI / 180.0);
      body.yRot = 0.0F;
      rightArm.z = 0.0F;
      rightArm.x = -5.0F;
      leftArm.z = 0.0F;
      leftArm.x = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)entity.getDeltaMovement().lengthSqr();
         f /= 0.2F;
         f *= f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      rightArm.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 2.0F * f2 * 0.5F / f;
      leftArm.xRot = Mth.cos(f1 * 0.6662F) * 2.0F * f2 * 0.5F / f;
      rightArm.zRot = 0.0F;
      leftArm.zRot = 0.0F;
      rightLeg.xRot = Mth.cos(f1 * 0.6662F) * 1.4F * f2 / f;
      leftLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 1.4F * f2 / f;
      rightLeg.yRot = 0.005F;
      leftLeg.yRot = -0.005F;
      rightLeg.zRot = 0.005F;
      leftLeg.zRot = -0.005F;
      rightArm.yRot = 0.0F;
      leftArm.yRot = 0.0F;
      if (entity.isCrouching()) {
         body.xRot = 0.5F;
         rightArm.xRot += 0.4F;
         leftArm.xRot += 0.4F;
         rightLeg.z = 4.0F;
         leftLeg.z = 4.0F;
         rightLeg.y = 12.2F;
         leftLeg.y = 12.2F;
         head.y = 4.2F;
         body.y = 3.2F;
         leftArm.y = 5.2F;
         rightArm.y = 5.2F;
      } else {
         body.xRot = 0.0F;
         rightLeg.z = 0.0F;
         leftLeg.z = 0.0F;
         rightLeg.y = 12.0F;
         leftLeg.y = 12.0F;
         head.y = 0.0F;
         body.y = 0.0F;
         leftArm.y = 2.0F;
         rightArm.y = 2.0F;
      }

      model.hat.copyFrom(head);
   }

   public static void animateArmsTiedFrontFirstPerson(
      LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, int lighting, float f1, float f2, boolean isLeftHand
   ) {
   }

   public static void animateArmsTiedBackFirstPerson(
      LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, int lighting, float f1, float f2, boolean isLeftHand
   ) {
   }
}
