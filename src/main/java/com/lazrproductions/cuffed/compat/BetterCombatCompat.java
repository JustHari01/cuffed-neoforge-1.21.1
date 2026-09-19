package com.lazrproductions.cuffed.compat;

import java.lang.reflect.Field;

public class BetterCombatCompat {
   private static boolean wasEnabled;
   private static boolean wasRecentlyDisabled;

   public static void load() {
   }

   public static void Disable() {
      if (!wasRecentlyDisabled) {
         try {
            Field enabled = Class.forName("net.bettercombat.client.BetterCombatClient").getField("ENABLED");
            wasEnabled = enabled.getBoolean(null);
            enabled.setBoolean(null, false);
            wasRecentlyDisabled = true;
         } catch (ReflectiveOperationException ignored) {
         }
      }
   }

   public static void Reset() {
      if (wasRecentlyDisabled) {
         try {
            Class.forName("net.bettercombat.client.BetterCombatClient").getField("ENABLED").setBoolean(null, wasEnabled);
         } catch (ReflectiveOperationException ignored) {
         }
         wasRecentlyDisabled = false;
      }
   }
}
