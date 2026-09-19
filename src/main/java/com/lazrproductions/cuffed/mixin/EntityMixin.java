package com.lazrproductions.cuffed.mixin;

import com.lazrproductions.cuffed.api.CuffedAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class EntityMixin {
   @Inject(
      method = {"isControlledByLocalInstance"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isControlledByLocalInstance(CallbackInfoReturnable<Boolean> callback) {
      Entity thisEntity = (Entity)(Object)this;
      if (thisEntity.getControllingPassenger() instanceof Player player
         && CuffedAPI.Capabilities.getRestrainableCapability(player).restraintsDisabledMovement()) {
         callback.setReturnValue(false);
      }
   }
}
