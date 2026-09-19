package com.lazrproductions.cuffed.restraints;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RestraintAPI {
   public static AbstractRestraint getRestraintFromTag(Provider provider, CompoundTag tag) {
      if (tag.contains("Id")) {
         AbstractRestraint r = getNewRestraintByKey(ResourceLocation.bySeparator(tag.getString("Id"), ':'));
         if (r != null) {
            r.deserializeNBT(provider, tag);
            return r;
         }
      }

      return null;
   }

   public static AbstractRestraint getNewRestraintByKey(ResourceLocation key) {
      return RestraintAPI.Registries.containsKey(key) ? RestraintAPI.Registries.get(key) : null;
   }

   public static AbstractRestraint getRestraintFromStack(ItemStack stack, RestraintType type, ServerPlayer player, ServerPlayer captor) {
      if (stack.getItem() != null) {
         AbstractRestraint restraintBase = RestraintAPI.Registries.get(stack.getItem(), type);
         if (restraintBase != null) {
            String className = restraintBase.getClass().getName();

            try {
               Object o = Class.forName(className).getConstructor(ItemStack.class, ServerPlayer.class, ServerPlayer.class).newInstance(stack, player, captor);
               if (o instanceof AbstractRestraint) {
                  return (AbstractRestraint)o;
               }
            } catch (Exception var8) {
               CuffedMod.LOGGER.info("Error getting new instance of restraint for stack " + stack.getHoverName().getString());
               return null;
            }
         }
      }

      return null;
   }

   public static boolean isRestraintItem(ItemStack stack) {
      return IsHeadRestraintItem(stack) || IsArmRestraintItem(stack) || IsLegRestraintItem(stack);
   }

   public static boolean IsHeadRestraintItem(ItemStack stack) {
      AbstractRestraint r = RestraintAPI.Registries.get(stack.getItem(), RestraintType.Head);
      return r != null && r.getType() == RestraintType.Head;
   }

   public static boolean IsArmRestraintItem(ItemStack stack) {
      AbstractRestraint r = RestraintAPI.Registries.get(stack.getItem(), RestraintType.Arm);
      return r != null && r.getType() == RestraintType.Arm;
   }

   public static boolean IsLegRestraintItem(ItemStack stack) {
      AbstractRestraint r = RestraintAPI.Registries.get(stack.getItem(), RestraintType.Leg);
      return r != null && r.getType() == RestraintType.Leg;
   }

   public static boolean canEquipRestriantItem(ItemStack stack, RestraintType type, ServerPlayer player, ServerPlayer captor) {
      AbstractRestraint r = RestraintAPI.Registries.get(stack.getItem(), type);
      return r != null ? r.canEquipRestraintItem(stack, player, captor) : false;
   }

   public static ArmRestraintAnimationFlags getArmAnimationFlagByKey(ResourceLocation key) {
      AbstractRestraint r = RestraintAPI.Registries.get(key);
      return r != null ? r.getArmAnimationFlags() : ArmRestraintAnimationFlags.NONE;
   }

   public static LegRestraintAnimationFlags getLegAnimationFlagByKey(ResourceLocation key) {
      AbstractRestraint r = RestraintAPI.Registries.get(key);
      return r != null ? r.getLegAnimationFlags() : LegRestraintAnimationFlags.NONE;
   }

   public static final class Registries {
      @Deprecated
      public static void register(Object registry) {
      }

      public static boolean containsKey(ResourceLocation key) {
         return ModRestraints.REGISTRY != null && ModRestraints.REGISTRY.containsKey(key);
      }

      public static boolean containsValue(AbstractRestraint restraint) {
         return ModRestraints.REGISTRY != null && ModRestraints.REGISTRY.getKey(restraint) != null;
      }

      public static AbstractRestraint get(ResourceLocation key) {
         return ModRestraints.REGISTRY != null ? (AbstractRestraint)ModRestraints.REGISTRY.get(key) : null;
      }

      public static AbstractRestraint get(Item restraintItem, RestraintType type) {
         for (Pair<Item, AbstractRestraint> pair : getAllRestraintItemsAndTheirRestraints()) {
            if (((Item)pair.getFirst()).equals(restraintItem) && ((AbstractRestraint)pair.getSecond()).getType() == type) {
               return (AbstractRestraint)pair.getSecond();
            }
         }

         return null;
      }

      public static List<AbstractRestraint> getAllRestraints() {
         ArrayList<AbstractRestraint> res = new ArrayList<>();
         if (ModRestraints.REGISTRY != null) {
            for (AbstractRestraint ent : ModRestraints.REGISTRY) {
               res.add(ent);
            }
         }

         return res;
      }

      public static List<Item> getAllRestraintItems() {
         ArrayList<Item> res = new ArrayList<>();
         if (ModRestraints.REGISTRY != null) {
            for (AbstractRestraint ent : ModRestraints.REGISTRY) {
               res.add(ent.getItem());
            }
         }

         return res;
      }

      public static List<Pair<Item, AbstractRestraint>> getAllRestraintItemsAndTheirRestraints() {
         ArrayList<Pair<Item, AbstractRestraint>> pairs = new ArrayList<>();
         if (ModRestraints.REGISTRY != null) {
            for (AbstractRestraint ent : ModRestraints.REGISTRY) {
               pairs.add(new Pair(ent.getItem(), ent));
            }
         }

         return pairs;
      }

      public static int size() {
         return ModRestraints.REGISTRY != null ? 1 : 0;
      }

      public static int total() {
         return ModRestraints.REGISTRY != null ? ModRestraints.REGISTRY.size() : 0;
      }

      public static class ConflictingRestraintItemAndTypeException extends RuntimeException {
         public ConflictingRestraintItemAndTypeException() {
            super("The Restraint Registry already contains a restraint for this item with the same restraint type!");
         }

         public ConflictingRestraintItemAndTypeException(ResourceLocation key, RestraintType type) {
            super("The Restraint Registry already contains a restraint for " + key + " with the same restraint type of " + type);
         }
      }

      public static class RestraintRegistryContainsKeyException extends RuntimeException {
         public RestraintRegistryContainsKeyException() {
            super("The Restraint Registry already contains this key!");
         }

         public RestraintRegistryContainsKeyException(ResourceLocation key) {
            super("The Restraint Registry already contains the key " + key);
         }
      }

      public static class RestraintRegistryContainsRestraintException extends RuntimeException {
         public RestraintRegistryContainsRestraintException() {
            super("The Restraint Registry already contains this restraint!");
         }

         public RestraintRegistryContainsRestraintException(ResourceLocation key) {
            super("The Restraint Registry already contains the restraint " + key);
         }
      }
   }
}
