package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.effect.WoundedEffect;
import com.lazrproductions.cuffed.init.ModEffects;
import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class BandageItem extends Item {
   public BandageItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
      if (player.isCrouching() && player.hasEffect(ModEffects.WOUNDED_EFFECT)) {
         WoundedEffect.treatEntity(player);
         player.getItemInHand(hand).shrink(1);
         return InteractionResultHolder.consume(player.getItemInHand(hand));
      } else {
         return super.use(level, player, hand);
      }
   }

   public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
      if (entity.hasEffect(ModEffects.WOUNDED_EFFECT)) {
         WoundedEffect.treatEntity(entity);
         stack.shrink(1);
         return InteractionResult.SUCCESS;
      } else {
         return super.interactLivingEntity(stack, player, entity, hand);
      }
   }
}
