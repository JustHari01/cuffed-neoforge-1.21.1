package com.lazrproductions.cuffed.blocks.entity.model;

import com.lazrproductions.cuffed.blocks.GuillotineBlock;
import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class GuillotineBlockEntityModel<T extends GuillotineBlockEntity> {
   private final ModelPart guillotine;
   float prevDownProgress = 0.0F;

   public GuillotineBlockEntityModel(ModelPart root) {
      this.guillotine = root.getChild("guillotine");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition guillotine = partdefinition.addOrReplaceChild("guillotine", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));
      PartDefinition blade = guillotine.addOrReplaceChild(
         "blade",
         CubeListBuilder.create()
            .texOffs(26, 25)
            .addBox(-5.0F, -2.0F, -3.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(26, 25)
            .addBox(3.0F, -2.0F, -3.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 7)
            .addBox(-6.0F, -4.0F, -3.0F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(16, 7)
            .mirror()
            .addBox(6.0F, -4.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 7)
            .addBox(-10.0F, -4.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      blade.addOrReplaceChild(
         "dry_blade",
         CubeListBuilder.create().texOffs(0, 21).addBox(-6.0F, 4.0F, -3.0F, 12.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      blade.addOrReplaceChild(
         "bloody_blade",
         CubeListBuilder.create().texOffs(0, 10).addBox(-6.0F, 4.0F, -3.0F, 12.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      blade.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(@Nonnull GuillotineBlockEntity entity, float partialTick) {
      this.guillotine
         .setRotation(
            0.0F, (((Direction)entity.getBlockState().getValue(GuillotineBlock.FACING)).toYRot() - 180.0F) * (float) (Math.PI / 180.0), (float) Math.PI
         );
      float offset = 0.0F;
      if (entity.isDown) {
         this.prevDownProgress = this.prevDownProgress + (float)Math.pow((double)this.prevDownProgress + 0.08, 2.0) / 1.32F;
      } else {
         this.prevDownProgress = Mth.lerp(partialTick * 0.1F, this.prevDownProgress, 0.0F);
      }

      this.prevDownProgress = Mth.clamp(this.prevDownProgress, 0.0F, 1.0F);
      offset = this.prevDownProgress * 12.0F;
      this.guillotine.setPos(8.0F, 8.0F - offset, 8.0F);
      this.guillotine.getChild("blade").getChild("dry_blade").visible = !entity.isBloody;
      this.guillotine.getChild("blade").getChild("bloody_blade").visible = entity.isBloody;
   }

   public void renderToBuffer(
      @Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      int color = (int)(alpha * 255.0F) << 24 | (int)(red * 255.0F) << 16 | (int)(green * 255.0F) << 8 | (int)(blue * 255.0F);
      this.guillotine.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
