package com.lazrproductions.cuffed.effect;

import net.minecraft.resources.ResourceLocation;

public class RestrainedEffectInstance {
   public static final ResourceLocation NEGATIVE_MINE_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_mine_speed");
   public static final ResourceLocation NEGATIVE_MOVE_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_movement_speed");
   public static final ResourceLocation NEGATIVE_SWIM_SPEED_ID = ResourceLocation.fromNamespaceAndPath("cuffed", "restrain_swim_speed");

   public static int encodeRestraintProperties(boolean noMining, boolean noItemUse, boolean noMovement, boolean noJumping) {
      int encodedValue = 0;
      if (noMining) {
         encodedValue |= 1;
      }

      if (noItemUse) {
         encodedValue |= 2;
      }

      if (noMovement) {
         encodedValue |= 4;
      }

      if (noJumping) {
         encodedValue |= 8;
      }

      return encodedValue;
   }

   public static boolean[] decodeRestraintProperties(int v) {
      return new boolean[]{(v & 1) != 0, (v & 2) != 0, (v & 4) != 0, (v & 8) != 0};
   }

   public static boolean decodeNoMining(int v) {
      boolean[] decodedValues = decodeRestraintProperties(v);
      return decodedValues[0];
   }

   public static boolean decodeNoItemUse(int v) {
      boolean[] decodedValues = decodeRestraintProperties(v);
      return decodedValues[1];
   }

   public static boolean decodeNoMovement(int v) {
      boolean[] decodedValues = decodeRestraintProperties(v);
      return decodedValues[2];
   }

   public static boolean decodeNoJumping(int v) {
      boolean[] decodedValues = decodeRestraintProperties(v);
      return decodedValues[3];
   }
}
