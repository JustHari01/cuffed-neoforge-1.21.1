package com.lazrproductions.cuffed.mixin;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import java.util.ArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MouseHandler.class})
public class MouseHandlerMixin {
   @Inject(
      method = {"onPress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onPress(long windowId, int keyCode, int scanCode, int f3, CallbackInfo callback) {
      Minecraft inst = Minecraft.getInstance();
      LocalPlayer player = inst.player;
      if (player != null && inst.screen == null) {
         if (inst.screen != null) {
            return;
         }

         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null && cap.isRestrained()) {
            cap.onMouseInput(player, keyCode, scanCode);

            for (int i : cap.gatherBlockedInputs()) {
               if (keyCode == i) {
                  callback.cancel();
                  return;
               }
            }
         }

         if (player instanceof IDetainableEntity detainable && detainable.getDetained() > -1) {
            for (int ix : getBlockedKeyCodesWhenDetained()) {
               if (keyCode == ix) {
                  callback.cancel();
                  return;
               }
            }
         }
      }
   }

   @Inject(
      method = {"onScroll"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onScroll(long windowId, double deltaX, double deltaY, CallbackInfo callback) {
      Minecraft inst = Minecraft.getInstance();
      LocalPlayer player = inst.player;
      if (player != null) {
         if (inst.screen != null) {
            return;
         }

         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null && cap.armsRestrained()) {
            callback.cancel();
            return;
         }

         if (player instanceof IDetainableEntity detainable && detainable.getDetained() > -1) {
            callback.cancel();
            return;
         }
      }
   }

   private static ArrayList<Integer> getBlockedKeyCodesWhenDetained() {
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
            String var6 = mapping.getName();
            switch (var6) {
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
}
