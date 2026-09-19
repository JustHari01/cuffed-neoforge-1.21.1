package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import javax.annotation.Nonnull;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class PrisonerTagItem extends Item {
   public PrisonerTagItem(Properties p) {
      super(p);
   }

   public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
      if (entity instanceof Player other) {
         if (hand == InteractionHand.MAIN_HAND && !player.level().isClientSide()) {
            INicknamable nicknamable = (INicknamable)other;
            other.awardStat((ResourceLocation)ModStatistics.TIMES_NICKNAMED.get(), 1);
            if (stack.has(DataComponents.CUSTOM_NAME)) {
               nicknamable.setNickname(stack.getHoverName());
            } else {
               nicknamable.setNickname(null);
            }

            player.awardStat(Stats.ITEM_USED.get((Item)ModItems.PRISONER_TAG.get()));
            stack.shrink(1);
         }

         return InteractionResult.sidedSuccess(player.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }
}
