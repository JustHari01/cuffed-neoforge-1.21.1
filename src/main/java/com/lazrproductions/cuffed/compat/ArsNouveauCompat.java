package com.lazrproductions.cuffed.compat;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ArsNouveauCompat {
   public static void load() {
   }

   public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
      try {
         Class<?> registryClass = Class.forName("com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry");
         Method getManaMethod = registryClass.getMethod("getMana", LivingEntity.class);
         Object lazyOptional = getManaMethod.invoke(null, player);
         if (lazyOptional != null) {
            Method orElseMethod = lazyOptional.getClass().getMethod("orElse", Object.class);
            Object manaCap = orElseMethod.invoke(lazyOptional, null);
            if (manaCap != null) {
               Method removeManaMethod = manaCap.getClass().getMethod("removeMana", double.class);
               removeManaMethod.invoke(manaCap, (double)amount);
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
      try {
         Class<?> registryClass = Class.forName("com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry");
         Method getManaMethod = registryClass.getMethod("getMana", LivingEntity.class);
         Object lazyOptional = getManaMethod.invoke(null, player);
         if (lazyOptional != null) {
            Method orElseMethod = lazyOptional.getClass().getMethod("orElse", Object.class);
            Object manaCap = orElseMethod.invoke(lazyOptional, null);
            if (manaCap != null) {
               Method getMaxManaMethod = manaCap.getClass().getMethod("getMaxMana");
               int max = (Integer)getMaxManaMethod.invoke(manaCap);
               double amount = (double)max * amountPercentage;
               Method removeManaMethod = manaCap.getClass().getMethod("removeMana", double.class);
               removeManaMethod.invoke(manaCap, amount);
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }
   }
}
