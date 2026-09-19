package com.lazrproductions.cuffed.restraints.base;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public interface IEnchantableRestraint {
   ItemEnchantments getEnchantments();

   void setEnchantments(ItemEnchantments var1);

   default boolean hasEnchantment(Holder<Enchantment> enchantment) {
      return this.getEnchantmentLevel(enchantment) > 0;
   }

   default int getEnchantmentLevel(Holder<Enchantment> enchantment) {
      return this.getEnchantments().getLevel(enchantment);
   }

   default void enchant(Holder<Enchantment> enchantment, int value) {
      Mutable builder = new Mutable(this.getEnchantments());
      builder.set(enchantment, value);
      this.setEnchantments(builder.toImmutable());
   }
}
