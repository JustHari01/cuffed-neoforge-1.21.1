package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingAddRecipe extends CustomRecipe {
   public KeyRingAddRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      ItemStack ringStack = null;
      ArrayList<ItemStack> keyStack = new ArrayList<>(0);

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (!stack.is((Item)ModItems.KEY.get()) && !stack.is((Item)ModItems.KEY_RING.get())) {
               return false;
            }

            if (ringStack == null && stack.is((Item)ModItems.KEY_RING.get())) {
               ringStack = stack;
            }

            if (stack.is((Item)ModItems.KEY.get())) {
               keyStack.add(stack);
            }
         }
      }

      return ringStack != null && ItemTagUtils.getOrCreateTag(ringStack).getInt("Keys") + keyStack.size() > CuffedMod.SERVER_CONFIG.MAX_KEYS_PER_RING.get()
         ? false
         : ringStack != null && keyStack.size() != 0;
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      ItemStack ringStack = null;
      ArrayList<ItemStack> keyStacks = new ArrayList<>(0);
      if (this.matches(input, null)) {
         for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
               if (stack.is((Item)ModItems.KEY_RING.get()) && ringStack == null) {
                  ringStack = stack;
               }

               if (stack.is((Item)ModItems.KEY.get())) {
                  keyStacks.add(stack);
               }
            }
         }

         if (ringStack != null) {
            ItemStack newStack = ringStack.copy();
            newStack.setCount(1);
            int keys = 0;
            CompoundTag tag = ItemTagUtils.getTag(newStack);
            if (tag != null) {
               keys = tag.getInt("Keys");
            }

            for (ItemStack stack : keyStacks) {
               if (ItemTagUtils.getOrCreateTag(stack).contains("Id")) {
                  KeyRingItem.addKey(newStack, stack);
               }
            }

            int finalKeys = keys;
            ItemTagUtils.updateTag(newStack, t -> t.putInt("Keys", finalKeys + keyStacks.size()));
            return newStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.KEY_RING_ADD.get();
   }
}
