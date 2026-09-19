package com.lazrproductions.cuffed.restraints.client;

import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.IBreakableRestraint;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ClientsideAbstractRestraintHandler {
   public final AbstractRestraint parent;
   protected int clientSidedDurability = 100;

   public ClientsideAbstractRestraintHandler(AbstractRestraint parent) {
      this.parent = parent;
   }

   public abstract void renderOverlay(Player var1, GuiGraphics var2, float var3, Window var4);

   public abstract Class<? extends HumanoidModel> getRenderedModel();

   public abstract ModelLayerLocation getRenderedModelLayer();

   public abstract ResourceLocation getRenderedModelTexture();

   public void onKeyInput(Player player, int keyCode, int action) {
      Minecraft instance = Minecraft.getInstance();
      if (action == 1 && this.parent instanceof IBreakableRestraint breakable) {
         breakable.attemptToBreak(player, keyCode, action, instance.options);
      }
   }

   public void onMouseInput(Player player, int keyCode, int action) {
      Minecraft instance = Minecraft.getInstance();
      if (action == 1 && this.parent instanceof IBreakableRestraint breakable) {
         breakable.attemptToBreak(player, keyCode, action, instance.options);
      }
   }
}
