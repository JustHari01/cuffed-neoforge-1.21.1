package com.lazrproductions.cuffed.recipes;

import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRecipes;
import com.lazrproductions.cuffed.items.BakedKeyMoldItem;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class KeyMoldBakeRecipe extends SmeltingRecipe {
   public KeyMoldBakeRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      super(group, category, ingredient, result, experience, cookingTime);
   }

   public boolean matches(@Nonnull SingleRecipeInput inv, @Nonnull Level worldIn) {
      ItemStack item = inv.getItem(0);
      return item.is((Item)ModItems.KEY_MOLD.get()) && ItemTagUtils.getOrCreateTag(item).contains("CopiedKey");
   }

   public ItemStack assemble(@Nonnull SingleRecipeInput inv, @Nonnull Provider access) {
      return BakedKeyMoldItem.createFromRawMold(inv.getItem(0));
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.KEY_MOLD_BAKE.get();
   }
}
