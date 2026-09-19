package com.lazrproductions.cuffed.mixin;

import com.lazrproductions.cuffed.restraints.client.layer.PilloryEntityLayer;
import com.lazrproductions.cuffed.restraints.client.layer.RestraintEntityLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerRenderer.class})
public class PlayerRendererMixin {
   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void constructor(Context context, boolean slim, CallbackInfo callback) {
      PlayerRenderer m = (PlayerRenderer)(Object)this;
      m.addLayer(new RestraintEntityLayer(m, context));
      m.addLayer(new PilloryEntityLayer(m, context.getItemInHandRenderer()));
   }
}
