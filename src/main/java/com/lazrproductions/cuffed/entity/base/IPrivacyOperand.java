package com.lazrproductions.cuffed.entity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public interface IPrivacyOperand {
   String RESTRAINING_RESTRICTION = "cuffed:restraining";
   String NICKNAMING_RESTRICTION = "cuffed:nicknaming";
   String ANCHORING_RESTRICTION = "cuffed:anchoring";
   String DETAINING_RESTRICTION = "cuffed:detaining";
   String TAG_RESTRICTIONS = "PrivacyRestrictions";
   String TAG_RESTRICTION_ID = "Id";
   String TAG_RESTRICTION = "RestrictionLevel";

   void setRestrainingRestrictions(IPrivacyOperand.PrivacyRestriction var1);

   void setNicknamingRestrictions(IPrivacyOperand.PrivacyRestriction var1);

   void setAnchoringRestrictions(IPrivacyOperand.PrivacyRestriction var1);

   void setDetainingRestrictions(IPrivacyOperand.PrivacyRestriction var1);

   void setRestriction(String var1, IPrivacyOperand.PrivacyRestriction var2);

   IPrivacyOperand.PrivacyRestriction getRestriction(String var1);

   IPrivacyOperand.PrivacyRestriction getRestrainingRestrictions();

   IPrivacyOperand.PrivacyRestriction getNicknamingRestrictions();

   IPrivacyOperand.PrivacyRestriction getAnchoringRestrictions();

   IPrivacyOperand.PrivacyRestriction getDetainingRestrictions();

   ListTag serializeRestrictions();

   void deserializeRestrictions(CompoundTag var1);

   void writeDefaultRestrictions();

   public static enum PrivacyRestriction {
      ALWAYS,
      ASK,
      ONLY_WHEN_RESTRAINED,
      NEVER;

      public static IPrivacyOperand.PrivacyRestriction fromInteger(int integer) {
         switch (integer) {
            case 0:
               return ALWAYS;
            case 1:
               return ASK;
            case 2:
               return ONLY_WHEN_RESTRAINED;
            default:
               return NEVER;
         }
      }

      public int toInteger() {
         switch (this) {
            case ALWAYS:
               return 0;
            case ASK:
               return 1;
            case ONLY_WHEN_RESTRAINED:
               return 2;
            default:
               return 3;
         }
      }
   }
}
