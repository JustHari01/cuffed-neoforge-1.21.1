package com.lazrproductions.cuffed.inventory;

import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class LupaFriskingSlot extends FriskingSlot {
   public LupaFriskingSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
   }

   @Override
   public boolean mayPlace(@Nonnull ItemStack stack) {
      return this.container.canPlaceItem(this.getSlotIndex(), stack);
   }
}
