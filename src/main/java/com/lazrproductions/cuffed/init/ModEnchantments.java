package com.lazrproductions.cuffed.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
   public static final ResourceKey<Enchantment> IMBUE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "imbue"));
   public static final ResourceKey<Enchantment> FAMINE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "famine"));
   public static final ResourceKey<Enchantment> SHROUD = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "shroud"));
   public static final ResourceKey<Enchantment> EXHAUST = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "exhaust"));
   public static final ResourceKey<Enchantment> SILENCE = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "silence"));
   public static final ResourceKey<Enchantment> BUOYANT = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("cuffed", "buoyant"));
}
