package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class KeyRingDisassembleRecipe extends CustomRecipe {
   public KeyRingDisassembleRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
      ItemStack ringStack = null;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (!stack.is((Item)ModItems.KEY_RING.get())) {
               return false;
            }

            ringStack = stack;
         }
      }

      if (ringStack != null) {
         CompoundTag tag = ItemTagUtils.getTag(ringStack);
         if (tag != null && tag.getInt("Keys") > 0) {
            return true;
         }
      }

      return false;
   }

   public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull Provider provider) {
      ItemStack ringStack = null;
      if (this.matches(input, null)) {
         for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is((Item)ModItems.KEY_RING.get())) {
               ringStack = stack;
            }
         }

         if (ringStack != null) {
            CompoundTag tag = ItemTagUtils.getOrCreateTag(ringStack);
            if (tag.contains("BoundLocks")) {
               ListTag boundKeysTag = tag.getList("BoundLocks", 10);
               if (boundKeysTag.size() > 0) {
                  ItemStack stack = ((Item)ModItems.KEY.get()).getDefaultInstance();
                  stack.setCount(1);
                  CompoundTag keyOnRingTag = boundKeysTag.getCompound(boundKeysTag.size() - 1);
                  KeyItem.setBoundId(stack, keyOnRingTag.getUUID("Id"));
                  if (keyOnRingTag.contains("Name")) {
                     CompoundTag t = new CompoundTag();
                     t.putString("Name", keyOnRingTag.getString("Name"));
                     ItemTagUtils.updateTag(stack, keyTag -> keyTag.put("display", t));
                  }

                  return stack;
               }
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.KEY_RING_DISASSEMBLE.get();
   }

   public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput input) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

      for (int i = 0; i < nonnulllist.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            ItemStack stackCopy = stack.copy();
            CompoundTag tag = ItemTagUtils.getOrCreateTag(stackCopy);
            if (stackCopy.is((Item)ModItems.KEY_RING.get()) && tag.contains("Keys") && tag.getInt("Keys") > 1) {
               int numOfKeys = tag.getInt("Keys");
               if (tag.contains("BoundLocks")) {
                  ListTag boundKeysTag = tag.getList("BoundLocks", 10);
                  if (boundKeysTag.size() > 0) {
                     boundKeysTag.remove(boundKeysTag.size() - 1);
                     tag.put("BoundLocks", boundKeysTag);
                     tag.putInt("Keys", numOfKeys - 1);
                  }
               }

               ItemTagUtils.setTag(stackCopy, tag);
               nonnulllist.set(i, stackCopy);
            }
         }
      }

      return nonnulllist;
   }
}
