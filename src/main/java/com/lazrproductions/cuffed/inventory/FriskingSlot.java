package com.lazrproductions.cuffed.inventory;

import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FriskingSlot extends Slot {
   private final BooleanSupplier isLupaSupplier;

   public FriskingSlot(Container container, int index, int x, int y) {
      this(container, index, x, y, () -> false);
   }

   public FriskingSlot(Container container, int index, int x, int y, BooleanSupplier isLupaSupplier) {
      super(container, index, x, y);
      this.isLupaSupplier = isLupaSupplier;
   }

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return this.isLupaSupplier.getAsBoolean() ? this.container.canPlaceItem(this.getSlotIndex(), stack) : false;
   }

   public void onTake(@Nonnull Player player, @Nonnull ItemStack stack) {
      super.onTake(player, stack);
   }
}
