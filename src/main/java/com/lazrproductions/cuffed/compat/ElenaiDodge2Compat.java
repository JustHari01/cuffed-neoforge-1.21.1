package com.lazrproductions.cuffed.compat;

import java.lang.reflect.Method;

public class ElenaiDodge2Compat {
   private static boolean wasDisabledRecently;

   public static void load() {
   }

   public static void Disable() {
      invokeCooldown(Integer.MAX_VALUE);
      wasDisabledRecently = true;
   }

   public static void Reset() {
      if (wasDisabledRecently) {
         invokeCooldown(10);
         wasDisabledRecently = false;
      }
   }

   private static void invokeCooldown(int value) {
      try {
         Method method = Class.forName("com.elenai.elenaidodge2.client.ED2ClientStorage").getMethod("setCooldown", int.class);
         method.invoke(null, value);
      } catch (ReflectiveOperationException ignored) {
      }
   }
}
