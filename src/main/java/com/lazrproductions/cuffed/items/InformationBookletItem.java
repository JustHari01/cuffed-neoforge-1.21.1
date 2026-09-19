package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.client.gui.screen.InformationBookletScreen;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class InformationBookletItem extends Item {
   public InformationBookletItem(Properties p) {
      super(p);
   }

   public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
      if (level.isClientSide()) {
         this.openBook();
      }

      return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext level, @Nonnull List<Component> components, @Nonnull TooltipFlag flag) {
      components.add(Component.translatable("item.cuffed.information_booklet.desc").withStyle(ChatFormatting.GRAY));
      super.appendHoverText(stack, level, components, flag);
   }

   @OnlyIn(Dist.CLIENT)
   void openBook() {
      Minecraft.getInstance().setScreen(new InformationBookletScreen(Minecraft.getInstance()));
   }
}
