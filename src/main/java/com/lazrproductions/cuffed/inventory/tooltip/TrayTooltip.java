package com.lazrproductions.cuffed.inventory.tooltip;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.TrayItem;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TrayTooltip implements ClientTooltipComponent, TooltipComponent {
   public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/tray.png");
   public static final ScreenTexture BACKGROUND_TEXTURE = new ScreenTexture(
      ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/tray.png"), 0.0F, 0.0F, 39, 20, 128, 128
   );
   public static final ScreenTexture FORK_TEXTURE = new ScreenTexture(
      ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/tray.png"), 0.0F, 20.0F, 5, 15, 128, 128
   );
   public static final ScreenTexture SPOON_TEXTURE = new ScreenTexture(
      ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/tray.png"), 5.0F, 20.0F, 5, 15, 128, 128
   );
   public static final ScreenTexture KNIFE_TEXTURE = new ScreenTexture(
      ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/tray.png"), 10.0F, 20.0F, 5, 15, 128, 128
   );
   private final NonNullList<ItemStack> items;

   public TrayTooltip(NonNullList<ItemStack> Items) {
      this.items = Items;
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public int getHeight() {
      return 20;
   }

   public int getWidth(@Nonnull Font font) {
      return 39;
   }

   public void renderImage(@Nonnull Font font, int x, int y, @Nonnull GuiGraphics gui) {
      ScreenUtilities.drawTexture(gui, new BlitCoordinates(x, y, 39, 20), BACKGROUND_TEXTURE);
      int foodSlot = this.getTrayFoodSlot();
      if (foodSlot > -1) {
         this.renderSlot(x + 1, y + 1, foodSlot, gui, font);
      }

      int spoons = this.hasSpoon();
      int forks = this.hasFork();
      int knifes = this.hasKnife();
      int nextX = 0;

      for (int i = 0; i < forks; i++) {
         ScreenUtilities.drawTexture(gui, new BlitCoordinates(x + 18 + nextX, y + 2, 5, 15), FORK_TEXTURE);
         nextX += 6;
      }

      for (int i = 0; i < spoons; i++) {
         ScreenUtilities.drawTexture(gui, new BlitCoordinates(x + 18 + nextX, y + 2, 5, 15), SPOON_TEXTURE);
         nextX += 6;
      }

      for (int i = 0; i < knifes; i++) {
         ScreenUtilities.drawTexture(gui, new BlitCoordinates(x + 18 + nextX, y + 2, 5, 15), KNIFE_TEXTURE);
         nextX += 6;
      }
   }

   private void renderSlot(int x, int y, int index, GuiGraphics gui, Font p_281863_) {
      ItemStack itemstack = (ItemStack)this.items.get(index);
      gui.renderItem(itemstack, x + 1, y + 1, index);
      gui.renderItemDecorations(p_281863_, itemstack, x + 1, y + 1);
   }

   private int getTrayFoodSlot() {
      for (int i = 0; i < this.items.size(); i++) {
         if (TrayItem.itemIsFood((ItemStack)this.items.get(i))) {
            return i;
         }
      }

      return -1;
   }

   private int hasSpoon() {
      int c = 0;

      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).is((Item)ModItems.SPOON.get())) {
            c++;
         }
      }

      return c;
   }

   private int hasFork() {
      int c = 0;

      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).is((Item)ModItems.FORK.get())) {
            c++;
         }
      }

      return c;
   }

   private int hasKnife() {
      int c = 0;

      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).is((Item)ModItems.KNIFE.get())) {
            c++;
         }
      }

      return c;
   }

   @OnlyIn(Dist.CLIENT)
   static enum Texture {
      SLOT(0, 0, 18, 20),
      BORDER_VERTICAL(0, 18, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
         this.x = p_169928_;
         this.y = p_169929_;
         this.w = p_169930_;
         this.h = p_169931_;
      }
   }
}
