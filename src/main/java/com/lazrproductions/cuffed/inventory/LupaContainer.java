package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LupaContainer implements Container {
   private final ServerPlayer player;

   private boolean isMainHandSlot(int index) {
      return index >= 36 && index <= 44 ? index - 36 == this.player.getInventory().selected : false;
   }

   public LupaContainer(ServerPlayer player) {
      this.player = player;
   }

   public ServerPlayer getServerPlayer() {
      return this.player;
   }

   public int getContainerSize() {
      return 54;
   }

   public boolean isEmpty() {
      return this.player.getInventory().isEmpty();
   }

   public boolean isInvalidSlot(int index) {
      return index >= 4 && index < 8;
   }

   public int getSlot(int index) {
      if (index == 8) {
         return 40;
      } else if (index >= 0 && index <= 3) {
         return 39 - index;
      } else if (index >= 9 && index <= 35) {
         return index;
      } else {
         return index >= 36 && index <= 44 ? index - 36 : -1;
      }
   }

   public ItemStack getItem(int index) {
      if (this.isInvalidSlot(index)) {
         return ItemStack.EMPTY;
      } else {
         int slot = this.getSlot(index);
         if (slot == -1) {
            return ItemStack.EMPTY;
         } else {
            ItemStack stack = this.player.getInventory().getItem(slot);
            return this.isMainHandSlot(index) && !stack.isEmpty() ? stack : stack;
         }
      }
   }

   public ItemStack removeItem(int index, int count) {
      if (this.isInvalidSlot(index)) {
         return ItemStack.EMPTY;
      } else if (this.isMainHandSlot(index)) {
         return ItemStack.EMPTY;
      } else {
         int slot = this.getSlot(index);
         if (slot > -1) {
            ItemStack stack = this.player.getInventory().removeItem(slot, count);
            this.setChanged();
            return stack;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public ItemStack removeItemNoUpdate(int index) {
      if (this.isInvalidSlot(index)) {
         return ItemStack.EMPTY;
      } else if (this.isMainHandSlot(index)) {
         return ItemStack.EMPTY;
      } else {
         int slot = this.getSlot(index);
         if (slot > -1) {
            ItemStack stack = this.player.getInventory().removeItemNoUpdate(slot);
            this.setChanged();
            return stack;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public void setItem(int index, @Nonnull ItemStack stack) {
      if (!this.isInvalidSlot(index)) {
         int slot = this.getSlot(index);
         if (slot != -1) {
            this.player.getInventory().setItem(slot, stack);
            this.setChanged();
         }
      }
   }

   public int getMaxStackSize() {
      return this.player.getInventory().getMaxStackSize();
   }

   public void setChanged() {
      this.player.getInventory().setChanged();
      this.player.containerMenu.broadcastChanges();
   }

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }

   public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
      return !this.isInvalidSlot(index);
   }

   public void clearContent() {
      this.player.getInventory().clearContent();
   }
}
