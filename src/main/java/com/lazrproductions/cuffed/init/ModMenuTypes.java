package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.inventory.FriskingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
   public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, "cuffed");
   public static final DeferredHolder<MenuType<?>, MenuType<FriskingMenu>> FRISKING_MENU = MENU_TYPES.register(
      "frisking_menu", () -> new MenuType(FriskingMenu::new, FeatureFlags.REGISTRY.allFlags())
   );

   public static void register(IEventBus eventBus) {
      MENU_TYPES.register(eventBus);
   }
}
