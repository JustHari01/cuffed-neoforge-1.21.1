package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.PosterBlockItem;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PosterChangeRecipe extends CustomRecipe {
   public PosterChangeRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      return this.isGridValid(input);
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      if (this.matches(input, null)) {
         ItemStack posterStack = this.getPosterFromGrid(input);
         return PosterBlockItem.newItemFromType(PosterBlockItem.getPosterType(posterStack).next());
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getPosterFromGrid(@Nonnull CraftingInput input) {
      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty() && checkingStack.is((Item)ModItems.POSTER_ITEM.get())) {
            return checkingStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean isGridValid(@Nonnull CraftingInput input) {
      int numOfPosters = 0;

      for (int i = 0; i < input.size(); i++) {
         ItemStack checkingStack = input.getItem(i);
         if (!checkingStack.isEmpty()) {
            if (!checkingStack.is((Item)ModItems.POSTER_ITEM.get())) {
               return false;
            }

            numOfPosters++;
         }
      }

      return numOfPosters == 1;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.POSTER_CHANGE.get();
   }
}
