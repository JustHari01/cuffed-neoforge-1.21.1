package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyMoldItem;
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

public class KeyMoldCopyRecipe extends CustomRecipe {
   public KeyMoldCopyRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      return this.isGridValid(input);
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      if (this.matches(input, null)) {
         ItemStack keyStack = this.getKeyStackFromGrid(input);
         return ItemTagUtils.getOrCreateTag(keyStack).contains("Id") ? KeyMoldItem.createFromKey(keyStack) : Items.CLAY_BALL.getDefaultInstance();
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getKeyStackFromGrid(@Nonnull CraftingInput input) {
      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty() && checkingStack.is((Item)ModItems.KEY.get())) {
            return checkingStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean isGridValid(@Nonnull CraftingInput input) {
      int numOfClayOrMolds = 0;
      int numOfKeys = 0;

      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty()) {
            if (checkingStack.is((Item)ModItems.KEY.get()) && ItemTagUtils.getOrCreateTag(checkingStack).contains("Id")) {
               numOfKeys++;
            } else {
               if (!checkingStack.is((Item)ModItems.KEY_MOLD.get()) && !checkingStack.is(Items.CLAY_BALL)) {
                  return false;
               }

               numOfClayOrMolds++;
            }
         }
      }

      return numOfKeys == 1 && numOfClayOrMolds == 1;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.KEY_MOLD_COPY.get();
   }

   public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack item = input.getItem(i);
         if (!item.isEmpty() && item.is((Item)ModItems.KEY.get())) {
            nonnulllist.set(i, item.copy());
         }
      }

      return nonnulllist;
   }
}
