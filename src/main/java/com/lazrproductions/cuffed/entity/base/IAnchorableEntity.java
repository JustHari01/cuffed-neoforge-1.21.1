package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface IAnchorableEntity {
   boolean isAnchored();

   Entity getAnchor();

   Entity getAnchorClientSide();

   void setAnchoredTo(@Nullable Entity var1);

   void setAnchor(@Nullable Entity var1);
}
