package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BakedKeyMoldCopyRecipe extends CustomRecipe {
   public BakedKeyMoldCopyRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      return this.isGridValid(input);
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      if (this.matches(input, null)) {
         ItemStack moldStack = this.getMoldFromGrid(input);
         return ItemTagUtils.getOrCreateTag(moldStack).contains("CopiedKey") ? BakedKeyMoldItem.createKeyFrom(moldStack, 1) : ItemStack.EMPTY;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getMoldFromGrid(@Nonnull CraftingInput input) {
      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty() && checkingStack.is((Item)ModItems.BAKED_KEY_MOLD.get())) {
            return checkingStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean isGridValid(@Nonnull CraftingInput input) {
      int numofMolds = 0;
      int numOfIngots = 0;

      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty()) {
            if (checkingStack.is((Item)ModItems.BAKED_KEY_MOLD.get()) && ItemTagUtils.getOrCreateTag(checkingStack).contains("CopiedKey")) {
               numofMolds++;
            } else {
               if (!checkingStack.is(Items.IRON_INGOT)) {
                  return false;
               }

               numOfIngots++;
            }
         }
      }

      return numOfIngots == 1 && numofMolds == 1;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.BAKED_KEY_MOLD_COPY.get();
   }

   public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack item = input.getItem(i);
         if (!item.isEmpty() && item.is((Item)ModItems.BAKED_KEY_MOLD.get())) {
            int oldQuality = ItemTagUtils.getOrCreateTag(item).getInt("Quality");
            if (oldQuality > 1) {
               ItemStack copy = item.copy();
               ItemTagUtils.updateTag(copy, tag -> tag.putInt("Quality", oldQuality - 1));
               nonnulllist.set(i, copy);
            }
         }
      }

      return nonnulllist;
   }
}
