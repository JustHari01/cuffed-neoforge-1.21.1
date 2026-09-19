package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class CreativeRestraintCutter extends Item {
   public CreativeRestraintCutter(Properties p) {
      super(p);
   }

   public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
      if (!player.level().isClientSide() && entity instanceof Player other) {
         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(other);
         if (cap != null && cap.isRestrained()) {
            cap.TryUnequipRestraint((ServerPlayer)other, (ServerPlayer)player, RestraintType.Head);
            cap.TryUnequipRestraint((ServerPlayer)other, (ServerPlayer)player, RestraintType.Arm);
            cap.TryUnequipRestraint((ServerPlayer)other, (ServerPlayer)player, RestraintType.Leg);
            return InteractionResult.sidedSuccess(player.level().isClientSide());
         }
      }

      return InteractionResult.PASS;
   }

   public boolean isFoil(@Nonnull ItemStack stack) {
      return true;
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext level, @Nonnull List<Component> lore, @Nonnull TooltipFlag flag) {
      lore.add(Component.translatable(this.getDescriptionId() + ".lore").withStyle(ChatFormatting.GRAY));
   }
}
