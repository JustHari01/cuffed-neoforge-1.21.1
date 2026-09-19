package com.lazrproductions.cuffed.items.base;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class AbstractArmRestraintItem extends AbstractRestraintItem {
   public AbstractArmRestraintItem(Properties p) {
      super(p);
   }

   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext level, @Nonnull List<Component> components, @Nonnull TooltipFlag tooltipFlag) {
      components.add(Component.translatable("info.cuffed.restraint_type.arm").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, level, components, tooltipFlag);
   }
}
