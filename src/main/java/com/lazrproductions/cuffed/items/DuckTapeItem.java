package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.items.base.AbstractRestraintItem;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class DuckTapeItem extends AbstractRestraintItem {
   public DuckTapeItem(Properties p) {
      super(p);
   }

   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext level, @Nonnull List<Component> components, @Nonnull TooltipFlag tooltipFlag) {
      components.add(Component.translatable("info.cuffed.restraint_type.head").withStyle(ChatFormatting.GRAY));
      components.add(Component.translatable("info.cuffed.restraint_type.arm").withStyle(ChatFormatting.GRAY));
      components.add(Component.translatable("info.cuffed.restraint_type.leg").withStyle(ChatFormatting.GRAY));
      components.add(Component.empty());
      components.add(
         Component.translatable("info.cuffed.restraint_type.my_key")
            .withStyle(ChatFormatting.GRAY)
            .append(" ")
            .append(Component.translatable("info.cuffed.empty_hand").withStyle(ChatFormatting.WHITE))
      );
      super.appendHoverText(stack, level, components, tooltipFlag);
   }
}
