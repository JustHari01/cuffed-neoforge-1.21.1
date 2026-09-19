package com.lazrproductions.cuffed.restraints.base;

import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractLegRestraint extends AbstractRestraint {
   static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 60.0F, 24.0F, 16, 16, 192, 192);

   public AbstractLegRestraint() {
   }

   public AbstractLegRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      super(stack, player, captor);
   }

   @Override
   public RestraintType getType() {
      return RestraintType.Leg;
   }

   @Override
   public ArrayList<Integer> getBlockedKeyCodes() {
      ArrayList<Integer> b = new ArrayList<>();
      Minecraft inst = Minecraft.getInstance();
      if (inst != null && inst.options != null) {
         b.add(inst.options.keyUp.getKey().getValue());
         b.add(inst.options.keyDown.getKey().getValue());
         b.add(inst.options.keyLeft.getKey().getValue());
         b.add(inst.options.keyRight.getKey().getValue());
         b.add(inst.options.keyJump.getKey().getValue());
         b.add(inst.options.keySprint.getKey().getValue());

         for (KeyMapping mapping : inst.options.keyMappings) {
            String var7 = mapping.getName();
            switch (var7) {
               case "key.parcool.Crawl":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.Breakfall":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.WallSlide":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.Dodge":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.Vault":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.Flipping":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.FastRun":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.ClingToCliff":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.HangDown":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.WallJump":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.QuickTurn":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.parcool.HorizontalWallRun":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.elenaidodge2.dodge":
                  b.add(mapping.getKey().getValue());
                  break;
               case "keybinds.combatroll.roll":
                  b.add(mapping.getKey().getValue());
                  break;
               case "key.epicfight.dodge":
                  b.add(mapping.getKey().getValue());
            }
         }

         return b;
      } else {
         return b;
      }
   }

   @Override
   public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int screenWidth = 28;
      int screenHeight = 28;
      int x = window.getGuiScaledWidth() / 2 - screenWidth / 2;
      int y = window.getGuiScaledHeight() / 2 - screenHeight - 30;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
      ArrayList<Component> c = new ArrayList<>();
      c.add(Component.translatable(this.getActionBarLabel()));
      ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
   }
}
