package com.lazrproductions.cuffed.blocks.base;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface ILockableBlock {
   BooleanProperty LOCKED = BooleanProperty.create("locked");
   BooleanProperty BOUND = BooleanProperty.create("bound");

   static boolean isLocked(@Nonnull BlockState state) {
      return (Boolean)state.getValue(LOCKED);
   }

   static void setIsLocked(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, boolean locked) {
      Level level = player.level();
      if (level != null) {
         state = (BlockState)state.setValue(LOCKED, locked);
         level.setBlock(pos, state, 1);
         level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
         player.displayClientMessage(Component.translatable("info.lock.toggle_" + (locked ? "on" : "off")), true);
      }
   }

   static boolean isBound(@Nonnull BlockState state) {
      return (Boolean)state.getValue(BOUND);
   }

   static boolean tryToBindToKey(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
      if (!isBound(state)) {
         Level level = player.level();
         if (level != null) {
         }
      }

      return false;
   }

   static void bindToKey(@Nonnull Player player, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull ItemStack stack) {
      Level level = player.level();
      if (level != null) {
      }
   }
}
