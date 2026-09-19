package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.client.particle.BloodDripParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "cuffed");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_DRIP_FALL_PARTICLE = PARTICLE_TYPES.register(
      "blood_drip", () -> new SimpleParticleType(true)
   );

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }

   public static void registerSprites(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)BLOOD_DRIP_FALL_PARTICLE.get(), BloodDripParticle.Provider::new);
   }
}
