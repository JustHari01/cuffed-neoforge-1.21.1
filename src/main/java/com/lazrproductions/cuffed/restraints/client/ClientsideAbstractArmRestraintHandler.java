package com.lazrproductions.cuffed.restraints.client;

import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class ClientsideAbstractArmRestraintHandler extends ClientsideAbstractRestraintHandler {
   static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 76.0F, 24.0F, 16, 16, 192, 192);

   public ClientsideAbstractArmRestraintHandler(AbstractRestraint parent) {
      super(parent);
   }

   @Override
   public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int screenWidth = 28;
      int screenHeight = 28;
      int x = window.getGuiScaledWidth() / 2 - screenWidth / 2;
      int y = window.getGuiScaledHeight() / 2 - screenHeight - 65;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
      ArrayList<Component> c = new ArrayList<>();
      c.add(Component.translatable(this.parent.getActionBarLabel()));
      ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
   }
}
