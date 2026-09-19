package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.effect.RestrainedEffect;
import com.lazrproductions.cuffed.effect.WoundedEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "cuffed");
   public static final DeferredHolder<MobEffect, MobEffect> RESTRAINED_EFFECT = MOB_EFFECTS.register(
      "restrained", () -> new RestrainedEffect(MobEffectCategory.HARMFUL, 0)
   );
   public static final DeferredHolder<MobEffect, MobEffect> WOUNDED_EFFECT = MOB_EFFECTS.register(
      "wounded", () -> new WoundedEffect(MobEffectCategory.HARMFUL, 0)
   );

   public static void register(IEventBus bus) {
      MOB_EFFECTS.register(bus);
   }
}
