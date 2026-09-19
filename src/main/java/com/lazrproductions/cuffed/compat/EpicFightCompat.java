package com.lazrproductions.cuffed.compat;

import net.minecraft.world.entity.player.Player;

public class EpicFightCompat {
   private static boolean wasEnabled;
   private static boolean wasRecentlyDisabled;

   public static void load() {
   }

   public static void Disable(Player player) {
      if (!wasRecentlyDisabled) {
         try {
            Class<?> capabilities = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities");
            Class<?> patchType = Class.forName("yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch");
            Object patch = capabilities.getMethod("getEntityPatch", Player.class, Class.class).invoke(null, player, patchType);
            if (patch != null && (Boolean)patch.getClass().getMethod("isEpicFightMode").invoke(patch)) {
               patch.getClass().getMethod("toVanillaMode", boolean.class).invoke(patch, true);
               wasEnabled = true;
               wasRecentlyDisabled = true;
            }
         } catch (ReflectiveOperationException ignored) {
         }
      }
   }

   public static void Reset(Player player) {
      if (wasRecentlyDisabled) {
         try {
            Class<?> capabilities = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities");
            Class<?> patchType = Class.forName("yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch");
            Object patch = capabilities.getMethod("getEntityPatch", Player.class, Class.class).invoke(null, player, patchType);
            if (patch != null && wasEnabled) {
               patch.getClass().getMethod("toEpicFightMode", boolean.class).invoke(patch, true);
            }
         } catch (ReflectiveOperationException ignored) {
         }
         wasRecentlyDisabled = false;
      }
   }
}
