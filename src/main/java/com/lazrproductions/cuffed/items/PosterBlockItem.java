package com.lazrproductions.cuffed.items;

import com.lazrproductions.cuffed.blocks.PosterBlock;
import com.lazrproductions.cuffed.blocks.base.PosterType;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PosterBlockItem extends BlockItem {
   public static final String POSTER_TYPE_TAG = "Poster";

   public PosterBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   @Nullable
   protected BlockState getPlacementState(@Nonnull BlockPlaceContext ctx) {
      BlockState state = super.getPlacementState(ctx);
      if (state != null && state.is((Block)ModBlocks.POSTER.get())) {
         state = (BlockState)state.setValue(PosterBlock.POSTER_TYPE, getPosterType(ctx.getItemInHand()));
      }

      return state;
   }

   public static PosterType getPosterType(@Nonnull ItemStack stack) {
      CompoundTag tag = ItemTagUtils.getTag(stack);
      return tag != null && tag.contains("Poster") ? PosterType.fromString(tag.getString("Poster")) : PosterType.NONE;
   }

   public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, lines, tooltipFlag);
      lines.add(
         Component.translatable(this.getDescriptionId() + "." + getPosterType(stack).getSerializedName())
            .withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC})
      );
   }

   public static ItemStack newItemFromType(PosterType type) {
      ItemStack stack = ((Item)ModItems.POSTER_ITEM.get()).getDefaultInstance().copyWithCount(1);
      ItemTagUtils.updateTag(stack, tag -> tag.putString("Poster", type.getSerializedName()));
      return stack;
   }
}
