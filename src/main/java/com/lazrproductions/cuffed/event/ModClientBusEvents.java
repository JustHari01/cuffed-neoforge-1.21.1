package com.lazrproductions.cuffed.event;

import com.lazrproductions.cuffed.client.gui.screen.GenericScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post;

@EventBusSubscriber(
   modid = "cuffed",
   value = {Dist.CLIENT}
)
public class ModClientBusEvents {
   @SubscribeEvent
   public static void onKeyInput(Key event) {
      Minecraft inst = Minecraft.getInstance();
      if (inst != null && inst.screen instanceof GenericScreen sc) {
         sc.handleKeyAction(event.getKey(), event.getAction());
         if (event.getKey() == 256) {
            sc.onClose();
            inst.setScreen(null);
            inst.setOverlay(null);
         }
      }
   }

   @SubscribeEvent
   public static void onMouseInput(Post event) {
      Minecraft inst = Minecraft.getInstance();
      if (inst != null && inst.screen instanceof GenericScreen sc) {
         sc.handleMouseAction(event.getButton(), event.getAction());
      }
   }
}
