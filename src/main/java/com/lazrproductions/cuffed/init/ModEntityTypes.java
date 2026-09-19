package com.lazrproductions.cuffed.init;

import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.CrumblingBlockEntity;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.entity.WeightedAnchorEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "cuffed");
   public static DeferredHolder<EntityType<?>, EntityType<ChainKnotEntity>> CHAIN_KNOT = ENTITY_TYPES.register(
      "chain_knot",
      () -> Builder.of((EntityType.EntityFactory<ChainKnotEntity>)(type, level) -> new ChainKnotEntity(type, level), MobCategory.MISC)
            .clientTrackingRange(10)
            .updateInterval(Integer.MAX_VALUE)
            .setShouldReceiveVelocityUpdates(false)
            .sized(0.375F, 0.5F)
            .canSpawnFarFromPlayer()
            .fireImmune()
            .build("chain_knot")
   );
   public static DeferredHolder<EntityType<?>, EntityType<PadlockEntity>> PADLOCK = ENTITY_TYPES.register(
      "padlock",
      () -> Builder.of((EntityType.EntityFactory<PadlockEntity>)(type, level) -> new PadlockEntity(type, level), MobCategory.MISC)
            .clientTrackingRange(10)
            .updateInterval(Integer.MAX_VALUE)
            .setShouldReceiveVelocityUpdates(false)
            .sized(0.375F, 0.1F)
            .canSpawnFarFromPlayer()
            .fireImmune()
            .build("padlock")
   );
   public static DeferredHolder<EntityType<?>, EntityType<WeightedAnchorEntity>> WEIGHTED_ANCHOR = ENTITY_TYPES.register(
      "weighted_anchor",
      () -> Builder.of((EntityType.EntityFactory<WeightedAnchorEntity>)(type, level) -> new WeightedAnchorEntity(type, level), MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .canSpawnFarFromPlayer()
            .fireImmune()
            .clientTrackingRange(10)
            .build("weighted_anchor")
   );
   public static DeferredHolder<EntityType<?>, EntityType<CrumblingBlockEntity>> CRUMBLING_BLOCK = ENTITY_TYPES.register(
      "crumbling_block", () -> Builder.of((EntityType.EntityFactory<CrumblingBlockEntity>)(type, level) -> new CrumblingBlockEntity(type, level), MobCategory.MISC).sized(0.3F, 0.3F).build("crumbling_block")
   );

   public static void register(IEventBus bus) {
      ENTITY_TYPES.register(bus);
   }

   public static void registerAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)WEIGHTED_ANCHOR.get(), WeightedAnchorEntity.createAttributes().build());
   }
}
