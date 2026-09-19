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

public abstract class AbstractArmRestraint extends AbstractRestraint {
   static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture ARMS_ICON = new ScreenTexture(WIDGETS, 76.0F, 24.0F, 16, 16, 192, 192);

   public AbstractArmRestraint() {
   }

   public AbstractArmRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      super(stack, player, captor);
   }

   @Override
   public RestraintType getType() {
      return RestraintType.Arm;
   }

   @Override
   public ArrayList<Integer> getBlockedKeyCodes() {
      ArrayList<Integer> b = new ArrayList<>();
      Minecraft inst = Minecraft.getInstance();
      if (inst != null && inst.options != null) {
         b.add(inst.options.keyAttack.getKey().getValue());
         b.add(inst.options.keyUse.getKey().getValue());
         b.add(inst.options.keyInventory.getKey().getValue());
         b.add(inst.options.keyDrop.getKey().getValue());

         for (KeyMapping i : inst.options.keyHotbarSlots) {
            b.add(i.getKey().getValue());
         }

         b.add(inst.options.keyInventory.getKey().getValue());
         b.add(inst.options.keyPickItem.getKey().getValue());
         b.add(inst.options.keySwapOffhand.getKey().getValue());

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
               case "key.parcool.HorizontalWallRun":
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
      int y = window.getGuiScaledHeight() / 2 - screenHeight - 65;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, screenWidth, screenHeight), ARMS_ICON);
      ArrayList<Component> c = new ArrayList<>();
      c.add(Component.translatable(this.getActionBarLabel()));
      ScreenUtilities.renderLabel(Minecraft.getInstance(), graphics, window.getGuiScaledWidth() / 2, y + screenHeight, c, 16579836);
   }
}
