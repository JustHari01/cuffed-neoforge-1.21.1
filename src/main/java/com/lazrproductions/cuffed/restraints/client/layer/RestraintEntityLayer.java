package com.lazrproductions.cuffed.restraints.client.layer;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.LivingEntity;

public class RestraintEntityLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
   private final HashMap<String, HumanoidModel<T>> restraintModels = new HashMap<>();
   private final HashMap<String, RestraintModelInterface> restraintModelInterfaces = new HashMap<>();

   public RestraintEntityLayer(RenderLayerParent<T, M> parent, Context context) {
      super(parent);
      this.restraintModels.clear();

      for (AbstractRestraint res : RestraintAPI.Registries.getAllRestraints()) {
         try {
            RestraintModelInterface in = res.getModelInterface();
            if (in != null) {
               if (in.getRenderedModel() != null) {
                  this.restraintModels
                     .put(
                        res.getId().toString(),
                        (HumanoidModel<T>)in.getRenderedModel().getConstructor(ModelPart.class).newInstance(context.bakeLayer(in.getRenderedModelLayer()))
                     );
               }

               this.restraintModelInterfaces.put(res.getId().toString(), in);
            }
         } catch (Exception var6) {
            CuffedMod.LOGGER.error("The restraint " + res.getId() + " uses a model that is not valid!");
         }
      }
   }

   public void render(
      @Nonnull PoseStack stack,
      @Nonnull MultiBufferSource buffer,
      int lighting,
      @Nonnull T entity,
      float p_116987_,
      float p_116988_,
      float p_116989_,
      float p_116990_,
      float p_116991_,
      float p_116992_
   ) {
      IRestrainableEntity res = (IRestrainableEntity)entity;
      if (res != null) {
         AbstractRestraint headRestraint = RestraintAPI.Registries.get(res.getHeadRestraintId());
         if (headRestraint != null && this.restraintModels.containsKey(headRestraint.getId().toString())) {
            HumanoidModel<T> model = this.restraintModels.get(headRestraint.getId().toString());
            RestraintModelInterface in = this.restraintModelInterfaces.get(headRestraint.getId().toString());
            if (model != null) {
               ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
               model.body.visible = true;
               this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
               if (res.getHeadIsEnchanted()) {
                  this.renderGlint(stack, buffer, lighting, model);
               }
            }
         }

         AbstractRestraint armRestraint = RestraintAPI.Registries.get(res.getArmRestraintId());
         if (armRestraint != null && this.restraintModels.containsKey(armRestraint.getId().toString())) {
            HumanoidModel<T> model = this.restraintModels.get(armRestraint.getId().toString());
            RestraintModelInterface in = this.restraintModelInterfaces.get(armRestraint.getId().toString());
            if (model != null) {
               ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
               model.body.visible = true;
               this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
               if (res.getArmsAreEnchanted()) {
                  this.renderGlint(stack, buffer, lighting, model);
               }
            }
         }

         AbstractRestraint legRestraint = RestraintAPI.Registries.get(res.getLegRestraintId());
         if (legRestraint != null && this.restraintModels.containsKey(legRestraint.getId().toString())) {
            HumanoidModel<T> model = this.restraintModels.get(legRestraint.getId().toString());
            RestraintModelInterface in = this.restraintModelInterfaces.get(legRestraint.getId().toString());
            if (model != null) {
               ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
               model.body.visible = true;
               this.renderModel(stack, buffer, lighting, model, false, 1.0F, 1.0F, 1.0F, in.getRenderedModelTexture());
               if (res.getArmsAreEnchanted()) {
                  this.renderGlint(stack, buffer, lighting, model);
               }
            }
         }
      }
   }

   private void renderModel(
      PoseStack stack,
      MultiBufferSource buffer,
      int partialTick,
      Model model,
      boolean p_289668_,
      float p_289678_,
      float p_289674_,
      float p_289693_,
      ResourceLocation resource
   ) {
      VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(resource), p_289668_);
      int color = ARGB32.color(255, (int)(p_289678_ * 255.0F), (int)(p_289674_ * 255.0F), (int)(p_289693_ * 255.0F));
      model.renderToBuffer(stack, vertexconsumer, partialTick, OverlayTexture.NO_OVERLAY, color);
   }

   private void renderGlint(PoseStack stack, MultiBufferSource buffer, int partialTick, Model model) {
      model.renderToBuffer(stack, buffer.getBuffer(RenderType.armorEntityGlint()), partialTick, OverlayTexture.NO_OVERLAY, -1);
   }
}
