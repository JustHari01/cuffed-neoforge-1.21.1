package com.lazrproductions.cuffed.inventory;

import com.lazrproductions.cuffed.items.PossessionsBox;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FriskingContainer implements Container {
   private final ServerPlayer player;
   private final ItemStack boxStack;

   public FriskingContainer(ServerPlayer player, ItemStack boxStack) {
      this.player = player;
      this.boxStack = boxStack;
   }

   public Player getPlayer() {
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

   public boolean canTakeItem(@Nonnull Container p_273520_, int p_272681_, @Nonnull ItemStack p_273702_) {
      return false;
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
         return slot == -1 ? ItemStack.EMPTY : this.player.getInventory().getItem(slot);
      }
   }

   public ItemStack removeItem(int index, int count) {
      if (this.isInvalidSlot(index)) {
         return ItemStack.EMPTY;
      } else {
         int slot = this.getSlot(index);
         if (slot > -1) {
            if (!this.player.getInventory().getItem(slot).isEmpty()) {
               PossessionsBox.add(this.boxStack, this.player.getInventory().getItem(slot));
               count = this.player.getInventory().getItem(slot).getCount();
            }

            this.player.getInventory().removeItem(slot, count);
         }

         return ItemStack.EMPTY;
      }
   }

   public ItemStack removeItemNoUpdate(int index) {
      if (this.isInvalidSlot(index)) {
         return ItemStack.EMPTY;
      } else {
         int slot = this.getSlot(index);
         if (slot > -1) {
            if (!this.player.getInventory().getItem(slot).isEmpty()) {
               PossessionsBox.add(this.boxStack, this.player.getInventory().getItem(slot));
            }

            this.player.getInventory().removeItemNoUpdate(slot);
         }

         return ItemStack.EMPTY;
      }
   }

   public void setItem(int index, @Nonnull ItemStack is) {
      if (!this.isInvalidSlot(index)) {
         int slot = this.getSlot(index);
         if (slot != -1) {
            this.player.getInventory().setItem(slot, is);
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
      return false;
   }

   public void clearContent() {
      this.player.getInventory().clearContent();
   }
}
