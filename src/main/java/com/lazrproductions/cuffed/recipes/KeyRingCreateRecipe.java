package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class KeyRingCreateRecipe extends CustomRecipe {
   public KeyRingCreateRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      ArrayList<ItemStack> keyStack = new ArrayList<>(0);

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (!stack.is((Item)ModItems.KEY.get())) {
               return false;
            }

            keyStack.add(stack);
         }
      }

      return keyStack.size() > 0;
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      ArrayList<ItemStack> keyStacks = new ArrayList<>(0);
      if (this.matches(input, null)) {
         for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is((Item)ModItems.KEY.get())) {
               keyStacks.add(stack);
            }
         }

         ItemStack newStack = new ItemStack((ItemLike)ModItems.KEY_RING.get());
         newStack.setCount(1);

         for (ItemStack stack : keyStacks) {
            if (ItemTagUtils.getOrCreateTag(stack).contains("Id")) {
               KeyRingItem.addKey(newStack, stack);
            }
         }

         ItemTagUtils.updateTag(newStack, tag -> tag.putInt("Keys", keyStacks.size()));
         return newStack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.KEY_RING_CREATE.get();
   }
}
