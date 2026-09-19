package com.lazrproductions.cuffed.entity.base;

import net.minecraft.resources.ResourceLocation;

public interface IRestrainableEntity {
   boolean isRestrained();

   int getRestraintCode();

   ResourceLocation getArmRestraintId();

   ResourceLocation getLegRestraintId();

   ResourceLocation getHeadRestraintId();

   boolean getArmsAreEnchanted();

   boolean getLegsAreEnchanted();

   boolean getHeadIsEnchanted();

   void setRestraintCode(int var1);

   void setArmRestraintId(ResourceLocation var1);

   void setLegRestraintId(ResourceLocation var1);

   void setHeadRestraintId(ResourceLocation var1);

   void setArmsEnchanted(boolean var1);

   void setLegsEnchanted(boolean var1);

   void setHeadEnchanted(boolean var1);
}
