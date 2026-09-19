package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Padlock extends Item {
   public Padlock(Properties p) {
      super(p);
   }

   public InteractionResult useOn(@Nonnull UseOnContext context) {
      Player player = context.getPlayer();
      if (player == null) {
         return InteractionResult.FAIL;
      } else {
         Level level = context.getLevel();
         BlockPos pos = context.getClickedPos();
         if (level.getBlockState(pos).is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
            if (PadlockEntity.getLockAt(level, pos) != null) {
               return InteractionResult.FAIL;
            } else {
               if (!level.isClientSide) {
                  player.level().playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS);
                  PadlockEntity.getOrCreateLockAt(level, pos, context.getClickedFace());
                  player.awardStat(Stats.ITEM_USED.get((Item)ModItems.PADLOCK.get()));
                  player.getItemInHand(context.getHand()).shrink(1);
               }

               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }
}
