package com.lazrproductions.cuffed.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModSounds {
   public static final SoundEvent HANDCUFFED = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "restraint.apply_handcuffs"));
   public static final SoundEvent SHACKLES_EQUIP = SoundEvent.createVariableRangeEvent(
      ResourceLocation.fromNamespaceAndPath("cuffed", "restraint.apply_shackles")
   );
   public static final SoundEvent PILLORY_USE = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "block.pillory.use"));
   public static final SoundEvent GUILLOTINE_USE = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "block.guillotine.use"));
   public static final SoundEvent SAFE_OPEN = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "block.safe.open"));
   public static final SoundEvent SAFE_CLOSE = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "block.safe.close"));
   public static final SoundEvent LUPA_SEARCH = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("cuffed", "item.lupa.search"));

   public static void register(RegisterEvent event) {
      event.register(Registries.SOUND_EVENT, x -> {
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "restraint.apply_handcuffs"), HANDCUFFED);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "restraint.apply_shackles"), SHACKLES_EQUIP);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "block.pillory.use"), PILLORY_USE);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "block.guillotine.use"), GUILLOTINE_USE);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "block.safe.open"), SAFE_OPEN);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "block.safe.close"), SAFE_CLOSE);
         x.register(ResourceLocation.fromNamespaceAndPath("cuffed", "item.lupa.search"), LUPA_SEARCH);
      });
   }
}
