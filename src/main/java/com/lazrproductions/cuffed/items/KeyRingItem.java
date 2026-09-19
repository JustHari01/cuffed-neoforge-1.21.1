package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.blocks.CellDoor;
import com.lazrproductions.cuffed.blocks.SafeBlock;
import com.lazrproductions.cuffed.blocks.entity.LockableBlockEntity;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class KeyRingItem extends Item {
   public static final String TAG_BOUND_LOCKS = "BoundLocks";
   public static final String TAG_KEYS = "Keys";

   public KeyRingItem(Properties p) {
      super(p);
   }

   public InteractionResult useOn(@Nonnull UseOnContext context) {
      if (context.getPlayer() == null) {
         return InteractionResult.FAIL;
      } else {
         Level level = context.getLevel();
         Player player = context.getPlayer();
         BlockState state = level.getBlockState(context.getClickedPos());
         if (player != null) {
            ItemStack stack = player.getItemInHand(context.getHand());
            if (!level.isClientSide && context.getHand() == InteractionHand.MAIN_HAND && canBindLock(stack)) {
               if (state.getBlock() instanceof CellDoor) {
                  BlockPos bottomPos = context.getClickedPos();
                  if (state.getValue(CellDoor.HALF) == DoubleBlockHalf.UPPER) {
                     bottomPos = bottomPos.below();
                     state = level.getBlockState(bottomPos);
                  }

                  if (level.getBlockEntity(bottomPos) instanceof LockableBlockEntity lockable && !lockable.hasBeenBound()) {
                     if (tryToAddBoundId(player, stack, lockable.getLockId(), "block.cuffed.cell_door")) {
                        lockable.bind();
                        player.awardStat(Stats.ITEM_USED.get((Item)ModItems.KEY.get()), 1);
                        return InteractionResult.SUCCESS;
                     }

                     return InteractionResult.FAIL;
                  }
               } else if (level.getBlockEntity(context.getClickedPos()) instanceof LockableBlockEntity lockable) {
                  if (!lockable.hasBeenBound() && tryToAddBoundId(player, stack, lockable.getLockId(), lockable.getLockName())) {
                     lockable.bind();
                     player.awardStat(Stats.ITEM_USED.get((Item)ModItems.KEY_RING.get()), 1);
                     return InteractionResult.SUCCESS;
                  }
               } else if (state.getBlock() instanceof SafeBlock
                  && level.getBlockEntity(context.getClickedPos()) instanceof SafeBlockEntity safe
                  && !safe.hasBeenBound()
                  && tryToAddBoundId(player, stack, safe.getLockId(), "block.cuffed.safe")) {
                  safe.bind();
                  player.awardStat(Stats.ITEM_USED.get((Item)ModItems.KEY_RING.get()), 1);
                  return InteractionResult.SUCCESS;
               }
            }
         }

         return InteractionResult.FAIL;
      }
   }

   public static void addBoundId(ItemStack stack, UUID id) {
      ItemTagUtils.updateTag(stack, compoundtag -> {
         ListTag listtag;
         if (compoundtag.contains("BoundLocks", 9)) {
            listtag = compoundtag.getList("BoundLocks", 10);
         } else {
            listtag = new ListTag();
         }

         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.putUUID("Id", id);
         listtag.add(compoundtag1);
         compoundtag.put("BoundLocks", listtag);
      });
   }

   public static void addKey(ItemStack stack, ItemStack key) {
      ItemTagUtils.updateTag(stack, compoundtag -> {
         ListTag listtag;
         if (compoundtag.contains("BoundLocks", 9)) {
            listtag = compoundtag.getList("BoundLocks", 10);
         } else {
            listtag = new ListTag();
         }

         CompoundTag compoundtag1 = new CompoundTag();
         CompoundTag keyTag = ItemTagUtils.getOrCreateTag(key);
         compoundtag1.putUUID("Id", keyTag.getUUID("Id"));
         if (keyTag.contains("display")) {
            compoundtag1.putString("Name", keyTag.getCompound("display").getString("Name"));
         }

         listtag.add(compoundtag1);
         compoundtag.put("BoundLocks", listtag);
      });
   }

   public static boolean tryToAddBoundId(Player player, ItemStack stack, UUID id, String lockName) {
      if (canBindLock(stack) && !hasBoundId(stack, id)) {
         addBoundId(stack, id);
         if (player.level().getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO)) {
            player.displayClientMessage(Component.translatable("item.cuffed.key.info.bound").append(Component.literal(id + "")), false);
         } else {
            player.displayClientMessage(Component.translatable("item.cuffed.key.info.bound").append(Component.translatable(lockName)), false);
         }

         player.playSound(SoundEvents.CHAIN_FALL, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public static void removeBoundId(ItemStack stack, UUID id) {
      ItemTagUtils.updateTag(stack, compoundtag -> {
         ListTag listtag;
         if (compoundtag.contains("BoundLocks", 9)) {
            listtag = compoundtag.getList("BoundLocks", 10);
         } else {
            listtag = new ListTag();
         }

         int index = getBoundIdIndex(stack, id);
         if (index >= 0) {
            listtag.remove(index);
         }

         compoundtag.put("BoundLocks", listtag);
      });
   }

   public static boolean hasBoundId(ItemStack stack, UUID id) {
      CompoundTag compoundTag = ItemTagUtils.getTag(stack);
      if (compoundTag == null) {
         return false;
      } else {
         if (compoundTag.contains("BoundLocks", 9)) {
            ListTag boundPos = compoundTag.getList("BoundLocks", 10);

            for (int i = 0; i < boundPos.size(); i++) {
               if (boundPos.getCompound(i).getUUID("Id").equals(id)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static int getBoundIdIndex(ItemStack stack, UUID id) {
      CompoundTag compoundTag = ItemTagUtils.getTag(stack);
      if (compoundTag == null) {
         return -1;
      } else {
         if (compoundTag.contains("BoundLocks", 9)) {
            ListTag boundPos = compoundTag.getList("BoundLocks", 10);

            for (int i = 0; i < boundPos.size(); i++) {
               if (boundPos.getCompound(i).getUUID("Id").equals(id)) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static boolean canBindLock(ItemStack stack) {
      CompoundTag compoundTag = ItemTagUtils.getTag(stack);
      if (compoundTag == null) {
         return true;
      } else {
         int bindings = 0;
         int keys = 0;
         if (compoundTag.contains("BoundLocks", 9)) {
            ListTag boundPos = compoundTag.getList("BoundLocks", 10);
            bindings = boundPos.size();
            if (compoundTag.contains("Keys")) {
               keys = compoundTag.getInt("Keys");
            }

            return bindings < keys;
         } else {
            return true;
         }
      }
   }

   public void appendHoverText(
      @Nonnull ItemStack stack, @Nonnull TooltipContext pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced
   ) {
      super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
      int amount = 0;
      CompoundTag tag = ItemTagUtils.getTag(stack);
      if (tag != null && tag.contains("Keys")) {
         amount = tag.getInt("Keys");
      }

      pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", new Object[]{amount}).withStyle(ChatFormatting.GRAY));
      int bindings = 0;
      if (tag != null && tag.contains("BoundLocks", 9)) {
         ListTag boundPos = tag.getList("BoundLocks", 10);
         bindings = boundPos.size();
      }

      if (bindings == amount) {
         pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", new Object[]{bindings}).withStyle(ChatFormatting.GRAY));
      } else {
         pTooltipComponents.add(Component.translatable("item.cuffed.key_ring.description.amount", new Object[]{bindings}).withStyle(ChatFormatting.DARK_GRAY));
      }
   }

   public ItemStack getDefaultInstance() {
      ItemStack itemstack = new ItemStack(this);
      ItemTagUtils.updateTag(itemstack, tag -> tag.putInt("Keys", 2));
      return itemstack;
   }

   public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int num, boolean boo) {
      CompoundTag tag = ItemTagUtils.getTag(stack);
      if (tag == null || !tag.contains("Keys")) {
         ItemTagUtils.updateTag(stack, t -> t.putInt("Keys", 1));
      }

      super.inventoryTick(stack, level, entity, num, boo);
   }
}
