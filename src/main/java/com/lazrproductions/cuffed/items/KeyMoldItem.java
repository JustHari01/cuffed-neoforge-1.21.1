package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

public class KeyMoldItem extends Item {
   public static final String TAG_COPIED_KEY = "CopiedKey";
   public static final String TAG_NAME = "Name";

   public KeyMoldItem(Properties properties) {
      super(properties);
   }

   public static ItemStack createFromKey(ItemStack keyStack) {
      ItemStack newMold = new ItemStack((ItemLike)ModItems.KEY_MOLD.get(), 1);
      CompoundTag keyTag = ItemTagUtils.getTag(keyStack);
      if (keyTag != null && keyTag.contains("Id")) {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("Id", keyTag.getUUID("Id"));
         if (keyTag.contains("display")) {
            tag.putString("Name", keyTag.getCompound("display").getString("Name"));
         }

         ItemTagUtils.updateTag(newMold, moldTag -> moldTag.put("CopiedKey", tag));
         return newMold;
      } else {
         return newMold;
      }
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public void appendHoverText(
      @Nonnull ItemStack stack, @Nonnull TooltipContext pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced
   ) {
      super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
      CompoundTag rootTag = ItemTagUtils.getTag(stack);
      CompoundTag tag = rootTag != null ? rootTag.getCompound("CopiedKey") : null;
      if (tag != null && !tag.isEmpty()) {
         pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.bound").withStyle(ChatFormatting.GRAY));
      } else {
         pTooltipComponents.add(Component.translatable("item.cuffed.key_mold.description.unbound").withStyle(ChatFormatting.GRAY));
      }
   }
}
