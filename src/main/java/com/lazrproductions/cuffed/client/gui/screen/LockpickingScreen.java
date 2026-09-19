package com.lazrproductions.cuffed.client.gui.screen;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Random;

@OnlyIn(Dist.CLIENT)
public class LockpickingScreen extends GenericScreen {
   static final float TARGET_BUFFER = 5.0F;
   public float secondsPerPhase = 1.0F;
   public int speedIncreasePerPhase = 10;
   public int progressPerPick = 8;
   public int type = 0;
   public int lockId = -1;
   public String restrainedUUID;
   public int restraintType;
   public BlockPos doorPos = null;
   int currentPhase = 0;
   float ticksLeftToPick = 20.0F;
   float currentTargetPick = 0.0F;
   float animationTick;
   boolean isAnimating = false;
   float lerpedGhostAngle = 0.0F;

   public LockpickingScreen(Minecraft instance) {
      super(instance);
      this.startLockpicking();
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.minecraft != null) {
         this.renderContent(this.minecraft, graphics, mouseX, mouseY, partialTick);
      }

      super.render(graphics, mouseX, mouseY, partialTick);
   }

   public void renderContent(@Nonnull Minecraft instance, @Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (!this.isAnimating) {
         if (this.ticksLeftToPick > 0.0F) {
            if (this.ticksLeftToPick >= 40.0F) {
               this.completeLockpicking(instance);
            }

            this.ticksLeftToPick = this.ticksLeftToPick - (float)(this.currentPhase + 1) * ((float)this.speedIncreasePerPhase / 200.0F);
         } else {
            this.failLockpicking(instance);
         }
      } else {
         this.animationTick -= partialTick;
         if (this.ticksLeftToPick < 0.0F && this.animationTick % 2.0F == 0.0F) {
            LocalPlayer player = instance.player;
            if (player != null) {
               player.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 0.7F);
            }
         }

         if (this.animationTick <= 0.0F) {
            this.animationTick = 0.0F;
            this.isAnimating = false;
         }
      }

      int centerScreenX = graphics.guiWidth() / 2;
      int centerScreenY = graphics.guiHeight() / 2;
      int shakeOffset = Mth.floor((Mth.sin(this.animationTick * 2.0F) - 0.5F) * 3.0F);
      int scale = 3;
      int originalSize = 32;
      int finalSize = originalSize * scale;
      ScreenUtilities.drawTexture(
         graphics,
         new BlitCoordinates(centerScreenX - finalSize / 2 + shakeOffset, centerScreenY - 16 * scale, finalSize, finalSize),
         new ScreenTexture(ResourceLocation.fromNamespaceAndPath("cuffed", "textures/item/padlock.png"), 0.0F, 0.0F, 16, 16, 16, 16)
      );
      ScreenUtilities.drawGenericProgressBarUpright(
         graphics,
         new BlitCoordinates(centerScreenX - finalSize, Mth.floor((float)centerScreenY - (float)finalSize / 2.0F), 4, finalSize),
         this.ticksLeftToPick / 40.0F,
         partialTick
      );
      int pickPosX = Mth.floor((float)centerScreenX - (float)finalSize * 0.75F) - 8;
      int pickPosY = Mth.floor((float)centerScreenY) - 6;
      float angleToMouse = 0.0F;
      Vec2 mouseDelta = new Vec2(
         Mth.clamp((float)(mouseX - centerScreenX), -100.0F, 100.0F) / 100.0F, Mth.clamp((float)(mouseY - centerScreenY - 16), -100.0F, 100.0F) / 100.0F
      );
      angleToMouse = (float)Mth.atan2((double)mouseDelta.y, (double)mouseDelta.x) * (180.0F / (float)Math.PI) + 225.0F;
      float angleToCurrentGhost = -(90.0F * (this.currentTargetPick / (this.secondsPerPhase * 20.0F))) + 360.0F;
      this.lerpedGhostAngle = Mth.lerp(partialTick * 4.0F, this.lerpedGhostAngle, angleToCurrentGhost);
      if (this.ticksLeftToPick < 40.0F) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
         RenderSystem.enableBlend();
         ScreenUtilities.drawTexture(
            graphics,
            new BlitCoordinates(pickPosX, pickPosY, finalSize, finalSize),
            this.lerpedGhostAngle,
            (float)(26 * scale),
            (float)(8 * scale),
            new ScreenTexture(ResourceLocation.fromNamespaceAndPath("cuffed", "textures/item/lockpick.png"), 0.0F, 0.0F, 16, 16, 16, 16)
         );
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)MathUtilities.invert01((double)this.animationTick / 5.0));
         ScreenUtilities.drawTexture(
            graphics,
            new BlitCoordinates(pickPosX, pickPosY, finalSize, finalSize),
            angleToMouse,
            (float)(26 * scale),
            (float)(8 * scale),
            new ScreenTexture(ResourceLocation.fromNamespaceAndPath("cuffed", "textures/item/lockpick.png"), 0.0F, 0.0F, 16, 16, 16, 16)
         );
      }

      if (this.lastMouseInput.getAction() == 0
         && this.lastMouseInput.getInput() == 0
         && angleToMouse > angleToCurrentGhost - 10.0F
         && angleToMouse < angleToCurrentGhost + 5.0F) {
         this.beginNewPhase(instance);
      }
   }

   void beginNewPhase(@Nonnull Minecraft instance) {
      this.currentPhase++;
      this.ticksLeftToPick = this.ticksLeftToPick + (float)this.progressPerPick;
      LocalPlayer player = instance.player;
      if (player != null) {
         player.playSound(SoundEvents.CHAIN_STEP, 1.0F, 0.7F + this.ticksLeftToPick / 40.0F);
      }

      this.animationTick = 5.0F;
      this.isAnimating = true;
      this.assignNewGhost();
   }

   void startLockpicking() {
      this.currentPhase = 0;
      this.ticksLeftToPick = 30.0F;
      this.assignNewGhost();
   }

   void failLockpicking(@Nonnull Minecraft instance) {
      LocalPlayer player = instance.player;
      if (player != null) {
         switch (this.type) {
            case 0:
               CuffedAPI.Networking.sendLockpickFinishPickingLockPacketToServer(true, this.lockId, player.getUUID());
               break;
            case 1:
               CuffedAPI.Networking.sendLockpickFinishPickingRestraintPacketToServer(true, this.restrainedUUID, this.restraintType, player.getUUID());
               break;
            case 2:
               CuffedAPI.Networking.sendLockpickFinishPickingCellDoorPacketToServer(true, this.doorPos, player.getUUID());
         }

         instance.setScreen(null);
      }
   }

   void completeLockpicking(@Nonnull Minecraft instance) {
      LocalPlayer player = instance.player;
      if (player != null) {
         switch (this.type) {
            case 0:
               CuffedAPI.Networking.sendLockpickFinishPickingLockPacketToServer(false, this.lockId, player.getUUID());
               break;
            case 1:
               CuffedAPI.Networking.sendLockpickFinishPickingRestraintPacketToServer(false, this.restrainedUUID, this.restraintType, player.getUUID());
               break;
            case 2:
               CuffedAPI.Networking.sendLockpickFinishPickingCellDoorPacketToServer(false, this.doorPos, player.getUUID());
         }

         instance.setScreen(null);
      }

      instance.setScreen(null);
   }

   void assignNewGhost() {
      Random r = new Random();
      this.currentTargetPick = r.nextFloat() * this.secondsPerPhase * 20.0F;
   }
}
