package com.lazrproductions.cuffed.entity.base;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public interface IDetainableEntity {
   void detainToBlock(@Nonnull Level var1, Vector3f var2, @Nonnull BlockPos var3, int var4, float var5);

   void undetain();

   int getDetained();

   void setDetained(int var1);

   float getDetainedRotation();

   void setDetainedRotation(float var1);

   BlockState getBlockDetainedTo(@Nonnull Level var1);

   void setBlockDetainedTo(@Nonnull BlockPos var1);

   Vector3f getDetainedPosition();

   void setDetainedPosition(Vector3f var1);
}
