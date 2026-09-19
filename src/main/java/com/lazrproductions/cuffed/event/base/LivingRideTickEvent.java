package com.lazrproductions.cuffed.event.base;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingRideTickEvent extends LivingEvent implements ICancellableEvent {
   Entity vehicle;

   public LivingRideTickEvent(LivingEntity entity, Entity vehicle) {
      super(entity);
      this.setVehicle(vehicle);
   }

   public void setVehicle(Entity entity) {
      this.vehicle = entity;
   }

   public Entity getVehicle() {
      return this.vehicle;
   }
}
