package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.init.ModMenuTypes;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.inventory.FriskingMenu;
import com.lazrproductions.cuffed.inventory.LupaContainer;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class LupaItem extends Item {
   public LupaItem(Properties properties) {
      super(properties);
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand hand) {
      if (interactionTarget instanceof Player targetPlayer) {
         if (!player.level().isClientSide) {
            ItemTagUtils.updateTag(stack, tag -> tag.putInt("SearchTarget", targetPlayer.getId()));
         }

         player.startUsingItem(hand);
         return InteractionResult.sidedSuccess(player.level().isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BRUSH;
   }

   public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
      if (living instanceof Player player) {
         if (!level.isClientSide) {
            CompoundTag tag = ItemTagUtils.getTag(stack);
            if (tag != null && tag.contains("SearchTarget")) {
               int targetId = tag.getInt("SearchTarget");
               if (level.getEntity(targetId) instanceof ServerPlayer targetPlayer) {
                  if (player.distanceToSqr(targetPlayer) > 9.0) {
                     player.stopUsingItem();
                  } else {
                     int usedDuration = this.getUseDuration(stack, living) - remainingUseDuration;
                     if (usedDuration % 5 == 0) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.LUPA_SEARCH, SoundSource.PLAYERS, 1.0F, 1.0F);
                     }

                     if (usedDuration >= 60) {
                        player.stopUsingItem();
                        ItemTagUtils.updateTag(stack, t -> t.remove("SearchTarget"));
                        player.openMenu(
                           new MenuProvider() {
                              public Component getDisplayName() {
                                 return targetPlayer.getDisplayName();
                              }

                              public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                                 return new FriskingMenu(
                                    (MenuType<?>)ModMenuTypes.FRISKING_MENU.get(),
                                    id,
                                    playerInventory,
                                    targetPlayer.getId(),
                                    new LupaContainer(targetPlayer),
                                    5
                                 );
                              }
                           }
                        );
                     }
                  }
               } else {
                  player.stopUsingItem();
               }
            } else {
               player.stopUsingItem();
            }
         }
      }
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeCharged) {
      if (!level.isClientSide) {
         ItemTagUtils.updateTag(stack, tag -> tag.remove("SearchTarget"));
      }

      super.releaseUsing(stack, level, living, timeCharged);
   }
}
