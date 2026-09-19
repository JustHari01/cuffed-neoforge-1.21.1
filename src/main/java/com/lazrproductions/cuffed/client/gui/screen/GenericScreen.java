package com.lazrproductions.cuffed.client.gui.screen;

import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericScreen extends Screen {
   Window window;
   int width;
   int height;
   int mouseX;
   int mouseY;
   protected GenericScreen.InputAction lastKeyInput = GenericScreen.InputAction.NONE;
   protected GenericScreen.InputAction lastMouseInput = GenericScreen.InputAction.NONE;

   protected GenericScreen(Component title) {
      super(title);
   }

   public GenericScreen(@Nonnull Minecraft instance) {
      super(Component.literal(""));
      this.minecraft = instance;
      this.window = this.minecraft.getWindow();
      this.width = this.window.getWidth();
      this.height = this.window.getHeight();
   }

   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      this.width = this.window.getGuiScaledWidth();
      this.height = this.window.getGuiScaledHeight();
      this.lastKeyInput = GenericScreen.InputAction.NONE;
      this.lastMouseInput = GenericScreen.InputAction.NONE;
   }

   public void tick() {
   }

   public void handleMouseAction(int mouseButton, int action) {
      this.lastMouseInput = new GenericScreen.InputAction(mouseButton, action);
   }

   public void handleKeyAction(int keyCode, int action) {
      this.lastKeyInput = new GenericScreen.InputAction(keyCode, action);
   }

   public void onClose() {
   }

   static class InputAction {
      int input;
      int action;
      static GenericScreen.InputAction NONE = new GenericScreen.InputAction(-1, -1);

      public InputAction(int input, int action) {
         this.input = input;
         this.action = action;
      }

      public int getInput() {
         return this.input;
      }

      public int getAction() {
         return this.action;
      }

      @Override
      public String toString() {
         return "InputAction(" + this.input + ", " + this.action + ")";
      }
   }
}
