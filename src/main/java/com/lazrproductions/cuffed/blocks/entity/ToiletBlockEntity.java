package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ToiletBlockEntity extends BlockEntity {
   private static final int INVENTORY_SIZE = 1;
   private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

   public ToiletBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.TRAY.get(), pos, state);
   }

   public InteractionResult use(
      @Nonnull BlockState state,
      @Nonnull Level level,
      @Nonnull BlockPos pos,
      @Nonnull Player interacting,
      @Nonnull InteractionHand hand,
      @Nonnull BlockHitResult hitResult
   ) {
      if (hand == InteractionHand.MAIN_HAND && !level.isClientSide()) {
         ItemStack stack = interacting.getItemInHand(hand);
         if (stack.isEmpty()) {
            ItemStack stackToRemove = this.getNextStack();
            if (this.removeNextItem()) {
               interacting.setItemInHand(hand, stackToRemove.copy());
               level.playSound(
                  (Player)null,
                  (double)((float)this.getBlockPos().getX() + 0.5F),
                  (double)this.getBlockPos().getY(),
                  (double)((float)this.getBlockPos().getZ() + 0.5F),
                  SoundEvents.BUCKET_FILL,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
               );
               this.sendUpdate(level, pos, state);
               return InteractionResult.CONSUME;
            }
         } else if (this.addItem(stack.copyWithCount(1))) {
            stack.shrink(1);
            level.playSound(
               (Player)null,
               (double)((float)this.getBlockPos().getX() + 0.5F),
               (double)this.getBlockPos().getY(),
               (double)((float)this.getBlockPos().getZ() + 0.5F),
               SoundEvents.BUCKET_EMPTY,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
            );
            this.sendUpdate(level, pos, state);
            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.FAIL;
   }

   @Nonnull
   public ItemStack getNextStack() {
      for (ItemStack itemStack : this.items) {
         if (!itemStack.isEmpty()) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean addItem(@Nonnull ItemStack stack) {
      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).isEmpty()) {
            this.items.set(i, stack);
            return true;
         }
      }

      return false;
   }

   protected boolean setItem(@Nonnull ItemStack stack, int index) {
      if (((ItemStack)this.items.get(index)).isEmpty()) {
         this.items.set(index, stack);
         return true;
      } else {
         return false;
      }
   }

   protected boolean removeNextItem() {
      int index = -1;

      for (int i = 0; i < this.items.size(); i++) {
         if (!((ItemStack)this.items.get(i)).isEmpty()) {
            index = i;
            break;
         }
      }

      return index < 0 ? false : this.removeItem(index);
   }

   protected boolean removeItem(int index) {
      if (index >= 0 && index < this.items.size()) {
         boolean removedSomething = !((ItemStack)this.items.get(index)).isEmpty();
         this.items.set(index, ItemStack.EMPTY);
         return removedSomething;
      } else {
         return false;
      }
   }

   protected void sendUpdate(Level level, BlockPos pos, BlockState state) {
      setChanged(level, pos, state);
      level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.saveAdditional(tag, provider);
      ListTag list = new ListTag();
      this.items.forEach(c -> list.add(ItemTagUtils.saveItem(c, provider)));
      tag.put("Items", list);
   }

   protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.loadAdditional(tag, provider);
      if (tag.contains("Items")) {
         ListTag list = tag.getList("Items", 10);
         this.items = NonNullList.withSize(list.size(), ItemStack.EMPTY);

         for (int i = 0; i < list.size(); i++) {
            this.items.set(i, ItemTagUtils.loadItem(list.getCompound(i), provider));
         }
      }
   }

   public List<ItemStack> getDrops(@Nonnull Level level, @Nonnull BlockPos pos) {
      List<ItemStack> stacks = List.of();

      for (ItemStack itemStack : this.items) {
         stacks.add(itemStack);
      }

      return stacks;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(@Nonnull Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, @Nonnull Provider provider) {
      super.onDataPacket(net, pkt, provider);
   }
}
