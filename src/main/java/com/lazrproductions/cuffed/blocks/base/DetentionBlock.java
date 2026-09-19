package com.lazrproductions.cuffed.blocks.base;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class DetentionBlock extends Block {
   public DetentionBlock(Properties properties) {
      super(properties);
   }

   public abstract boolean canDetainPlayer(@Nonnull Level var1, @Nonnull BlockState var2, @Nonnull BlockPos var3, @Nonnull Player var4, boolean var5);
}
