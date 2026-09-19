package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.blocks.base.PosterType;
import com.lazrproductions.cuffed.items.PosterBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "cuffed");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUFFED_TAB = CREATIVE_MODE_TABS.register(
      "cuffed_tab",
      () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cuffed"))
            .icon(() -> ((Item)ModItems.HANDCUFFS.get()).getDefaultInstance())
            .displayItems((parameters, output) -> {
               output.accept((ItemLike)ModItems.HANDCUFFS.get());
               output.accept((ItemLike)ModItems.SHACKLES.get());
               output.accept((ItemLike)ModItems.FUZZY_HANDCUFFS.get());
               output.accept((ItemLike)ModItems.HANDCUFFS_KEY.get());
               output.accept((ItemLike)ModItems.SHACKLES_KEY.get());
               output.accept(Items.BUNDLE);
               output.accept((ItemLike)ModItems.POSSESSIONSBOX.get());
               output.accept((ItemLike)ModItems.PRISONER_TAG.get());
               output.accept((ItemLike)ModItems.DUCK_TAPE.get());
               output.accept((ItemLike)ModItems.BANDAGE.get());
               output.accept((ItemLike)ModItems.TRAY.get());
               output.accept((ItemLike)ModItems.FORK.get());
               output.accept((ItemLike)ModItems.SPOON.get());
               output.accept((ItemLike)ModItems.KNIFE.get());
               output.accept((ItemLike)ModItems.PADLOCK.get());
               output.accept((ItemLike)ModItems.KEY.get());
               output.accept((ItemLike)ModItems.KEY_RING.get());
               output.accept((ItemLike)ModItems.KEY_MOLD.get());
               output.accept((ItemLike)ModItems.BAKED_KEY_MOLD.get());
               output.accept((ItemLike)ModItems.LOCKPICK.get());
               output.accept((ItemLike)ModItems.LUPA.get());
               output.accept((ItemLike)ModItems.WEIGHTED_ANCHOR_ITEM.get());
               output.accept((ItemLike)ModItems.CELL_DOOR_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_BARS_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_BARS_GAPPED_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_SMOOTH_STONE_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_STONE_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_STONE_SLAB_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_STONE_STAIRS_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_STONE_CHISELED_ITEM.get());
               output.accept((ItemLike)ModItems.REINFORCED_LAMP_ITEM.get());
               output.accept((ItemLike)ModItems.PILLORY_ITEM.get());
               output.accept((ItemLike)ModItems.GUILLOTINE_ITEM.get());
               output.accept((ItemLike)ModItems.BUNK_ITEM.get());
               output.accept((ItemLike)ModItems.SAFE_ITEM.get());
               output.accept(PosterBlockItem.newItemFromType(PosterType.NONE));
               output.accept(PosterBlockItem.newItemFromType(PosterType.SERENITY));
               output.accept(PosterBlockItem.newItemFromType(PosterType.SKELETON));
               output.accept(PosterBlockItem.newItemFromType(PosterType.IMPUNITY));
               output.accept(PosterBlockItem.newItemFromType(PosterType.ZOOOM));
               output.accept(PosterBlockItem.newItemFromType(PosterType.A_SHADOW_LOCKED_AWAY));
               output.accept(PosterBlockItem.newItemFromType(PosterType.PRISONER));
               output.accept(PosterBlockItem.newItemFromType(PosterType.LANTERN));
               output.accept((ItemLike)ModItems.CREATIVE_RESTRAINT_CUTTER.get());
               output.accept((ItemLike)ModItems.CREATIVE_KEY.get());
               output.accept((ItemLike)ModItems.CREATIVE_BIND_BREAKER.get());
            })
            .build()
   );

   public static void register(IEventBus bus) {
      CREATIVE_MODE_TABS.register(bus);
   }
}
