package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.List;
import java.util.Random;
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

public class BakedKeyMoldItem extends Item {
   public static final String TAG_QUALITY = "Quality";

   public BakedKeyMoldItem(Properties properties) {
      super(properties);
   }

   public static ItemStack createFromRawMold(ItemStack oldMold) {
      ItemStack newMold = new ItemStack((ItemLike)ModItems.BAKED_KEY_MOLD.get(), 1);
      CompoundTag oldRoot = ItemTagUtils.getOrCreateTag(oldMold);
      CompoundTag oldTag = oldRoot.getCompound("CopiedKey");
      CompoundTag tag = new CompoundTag();
      tag.putUUID("Id", oldTag.getUUID("Id"));
      tag.putString("Name", oldTag.getString("Name"));
      Random r = new Random();
      int q = r.nextInt(1) + 4;
      ItemTagUtils.updateTag(newMold, newMoldTag -> {
         newMoldTag.put("CopiedKey", tag);
         newMoldTag.putInt("Quality", q);
      });
      return newMold;
   }

   public static ItemStack createKeyFrom(ItemStack moldStack, int amount) {
      ItemStack newKey = new ItemStack((ItemLike)ModItems.KEY.get(), amount);
      CompoundTag moldRoot = ItemTagUtils.getTag(moldStack);
      if (moldRoot != null && moldRoot.contains("CopiedKey")) {
         CompoundTag moldTag = moldRoot.getCompound("CopiedKey");
         ItemTagUtils.updateTag(newKey, keyTag -> {
            keyTag.putUUID("Id", moldTag.getUUID("Id"));
            if (moldTag.contains("Name")) {
               CompoundTag display = new CompoundTag();
               display.putString("Name", moldTag.getString("Name"));
               keyTag.put("display", display);
            }
         });
         return newKey;
      } else {
         return newKey;
      }
   }

   public ItemStack getDefaultInstance() {
      ItemStack itemstack = new ItemStack(this);
      ItemTagUtils.updateTag(itemstack, tag -> tag.putInt("Quality", 5));
      return itemstack;
   }

   public void appendHoverText(
      @Nonnull ItemStack stack, @Nonnull TooltipContext pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced
   ) {
      super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
      int quality = ItemTagUtils.getOrCreateTag(stack).getInt("Quality");
      pTooltipComponents.add(Component.translatable("item.cuffed.baked_key_mold.description.quality_" + quality).withStyle(ChatFormatting.DARK_GRAY));
   }
}
