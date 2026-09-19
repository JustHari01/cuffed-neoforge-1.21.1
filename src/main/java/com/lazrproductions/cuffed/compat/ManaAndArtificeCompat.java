package com.lazrproductions.cuffed.compat;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ManaAndArtificeCompat {
   public static void load() {
   }

   public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
      try {
         Class<?> modClass = Class.forName("com.mna.api.ManaAndArtificeMod");
         Method getCapMethod = modClass.getMethod("getMagicCapability");
         Object capability = getCapMethod.invoke(null);
         if (capability != null) {
            Method getCapabilityMethod = player.getClass().getMethod("getCapability", capability.getClass(), Class.forName("net.minecraft.core.Direction"));
            Object magic = getCapabilityMethod.invoke(player, capability, null);
            if (magic != null) {
               Method getCastingResourceMethod = magic.getClass().getMethod("getCastingResource");
               Object resource = getCastingResourceMethod.invoke(magic);
               if (resource != null) {
                  Method consumeMethod = resource.getClass().getMethod("consume", Player.class, float.class);
                  consumeMethod.invoke(resource, player, (float)amount);
               }
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
      try {
         Class<?> modClass = Class.forName("com.mna.api.ManaAndArtificeMod");
         Method getCapMethod = modClass.getMethod("getMagicCapability");
         Object capability = getCapMethod.invoke(null);
         if (capability != null) {
            Method getCapabilityMethod = player.getClass().getMethod("getCapability", capability.getClass(), Class.forName("net.minecraft.core.Direction"));
            Object magic = getCapabilityMethod.invoke(player, capability, null);
            if (magic != null) {
               Method getCastingResourceMethod = magic.getClass().getMethod("getCastingResource");
               Object resource = getCastingResourceMethod.invoke(magic);
               if (resource != null) {
                  Method getMaxAmountMethod = resource.getClass().getMethod("getMaxAmount");
                  float max = (Float)getMaxAmountMethod.invoke(resource);
                  Method consumeMethod = resource.getClass().getMethod("consume", Player.class, float.class);
                  consumeMethod.invoke(resource, player, max * (float)amountPercentage);
               }
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }
   }
}
