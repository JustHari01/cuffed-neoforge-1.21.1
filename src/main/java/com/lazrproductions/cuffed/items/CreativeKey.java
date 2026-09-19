package com.lazrproductions.cuffed.items;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class CreativeKey extends Item {
   public CreativeKey(Properties p) {
      super(p);
   }

   public boolean isFoil(@Nonnull ItemStack stack) {
      return true;
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext level, @Nonnull List<Component> lore, @Nonnull TooltipFlag flag) {
      lore.add(Component.translatable(this.getDescriptionId() + ".lore").withStyle(ChatFormatting.GRAY));
   }
}
