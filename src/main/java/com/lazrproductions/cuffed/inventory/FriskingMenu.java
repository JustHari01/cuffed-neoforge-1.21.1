package com.lazrproductions.cuffed.inventory;

import com.lazrproductions.cuffed.init.ModMenuTypes;
import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FriskingMenu extends AbstractContainerMenu {
   public final DataSlot isLupaMenu = DataSlot.standalone();
   private final Container container;
   private final int containerRows;
   private final int otherPlayerId;

   private FriskingMenu(MenuType<?> type, int containerId, Inventory otherPlayerInv, int otherPlayerId, int rows) {
      this(type, containerId, otherPlayerInv, otherPlayerId, new SimpleContainer(9 * rows), rows);
   }

   public FriskingMenu(MenuType<?> type, int containerId, Inventory playerInv, int otherPlayerId, Container container, int rows) {
      super(type, containerId);
      checkContainerSize(container, rows * 9);
      this.otherPlayerId = otherPlayerId;
      this.container = container;
      this.containerRows = rows;
      this.addDataSlot(this.isLupaMenu);
      this.isLupaMenu.set(container instanceof LupaContainer ? 1 : 0);
      container.startOpen(playerInv.player);
      this.addSlot(this.createFriskingSlot(container, 0, 8, 8));
      this.addSlot(this.createFriskingSlot(container, 1, 8, 26));
      this.addSlot(this.createFriskingSlot(container, 2, 8, 44));
      this.addSlot(this.createFriskingSlot(container, 3, 8, 62));
      this.addSlot(this.createFriskingSlot(container, 8, 77, 62));

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(this.createFriskingSlot(container, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }

      for (int column = 0; column < 9; column++) {
         this.addSlot(this.createFriskingSlot(container, 36 + column, 8 + column * 18, 142));
      }

      for (int l = 0; l < 3; l++) {
         for (int j1 = 0; j1 < 9; j1++) {
            this.addSlot(new Slot(playerInv, j1 + l * 9 + 9, 8 + j1 * 18, 174 + l * 18));
         }
      }

      for (int i1 = 0; i1 < 9; i1++) {
         this.addSlot(new Slot(playerInv, i1, 8 + i1 * 18, 232));
      }
   }

   public FriskingMenu(int containerId, Inventory inv) {
      this((MenuType<?>)ModMenuTypes.FRISKING_MENU.get(), containerId, inv, 0, 5);
   }

   public boolean stillValid(@Nonnull Player player) {
      Level level = player.level();
      if (level != null) {
         if (!(level.getEntity(this.otherPlayerId) instanceof Player other)) {
            return false;
         }

         if (other.isRemoved()) {
            return false;
         }

         if (player.distanceToSqr(other) > 4.0) {
            return false;
         }
      }

      return this.container.stillValid(player);
   }

   public ItemStack quickMoveStack(@Nonnull Player player, int count) {
      return ItemStack.EMPTY;
   }

   public boolean canDragTo(@Nonnull Slot slot) {
      return false;
   }

   public void removed(@Nonnull Player player) {
      super.removed(player);
      this.container.stopOpen(player);
   }

   public Container getContainer() {
      return this.container;
   }

   public int getRowCount() {
      return this.containerRows;
   }

   private Slot createFriskingSlot(Container container, int index, int x, int y) {
      return (Slot)(container instanceof LupaContainer
         ? new LupaFriskingSlot(container, index, x, y)
         : new FriskingSlot(container, index, x, y, () -> this.isLupaMenu.get() == 1));
   }
}
