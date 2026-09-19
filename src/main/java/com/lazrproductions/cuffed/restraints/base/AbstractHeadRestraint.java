package com.lazrproductions.cuffed.restraints.base;

import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractHeadRestraint extends AbstractRestraint {
   static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 60.0F, 40.0F, 16, 16, 192, 192);

   public AbstractHeadRestraint() {
   }

   public AbstractHeadRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      super(stack, player, captor);
   }

   @Override
   public RestraintType getType() {
      return RestraintType.Head;
   }

   @Override
   public ArrayList<Integer> getBlockedKeyCodes() {
      return new ArrayList<>();
   }

   @Override
   public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int screenWidth = 28;
      int screenHeight = 28;
      int x = window.getGuiScaledWidth() / 2 - screenWidth / 2;
      int y = window.getGuiScaledHeight() / 2 - screenHeight - 100;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
      ArrayList<Component> c = new ArrayList<>();
      c.add(Component.translatable(this.getActionBarLabel()));
      ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
   }
}
