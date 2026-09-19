package com.lazrproductions.cuffed.utils;

import java.util.function.Consumer;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ItemTagUtils {
   public static CompoundTag getTag(ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      } else {
         CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
         return customData != null ? customData.copyTag() : null;
      }
   }

   public static CompoundTag getOrCreateTag(ItemStack stack) {
      if (stack.isEmpty()) {
         return new CompoundTag();
      } else {
         CustomData customData = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
         return customData != null ? customData.copyTag() : new CompoundTag();
      }
   }

   public static void setTag(ItemStack stack, CompoundTag tag) {
      if (!stack.isEmpty()) {
         if (tag != null && !tag.isEmpty()) {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         } else {
            stack.remove(DataComponents.CUSTOM_DATA);
         }
      }
   }

   public static void updateTag(ItemStack stack, Consumer<CompoundTag> updater) {
      if (!stack.isEmpty()) {
         CompoundTag tag = getOrCreateTag(stack);
         updater.accept(tag);
         setTag(stack, tag);
      }
   }

   public static Provider getProvider() {
      if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         return server != null ? server.registryAccess() : null;
      } else {
         try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mcInstance = mcClass.getMethod("getInstance").invoke(null);
            Object level = mcClass.getField("level").get(mcInstance);
            if (level != null) {
               return (Provider)level.getClass().getMethod("registryAccess").invoke(level);
            }
         } catch (Exception var3) {
         }

         return null;
      }
   }

   public static ItemStack loadItem(CompoundTag tag, Provider provider) {
      if (tag != null && !tag.isEmpty()) {
         if (provider == null) {
            provider = getProvider();
         }

         return provider == null ? ItemStack.EMPTY : ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static CompoundTag saveItem(ItemStack stack, Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (stack.isEmpty()) {
         return tag;
      } else {
         if (provider == null) {
            provider = getProvider();
         }

         return provider == null ? tag : (CompoundTag)stack.save(provider, tag);
      }
   }
}
