package com.lazrproductions.cuffed.client.gui.screen;

import com.lazrproductions.cuffed.inventory.FriskingMenu;
import com.lazrproductions.cuffed.inventory.FriskingSlot;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class FriskingScreen extends AbstractContainerScreen<FriskingMenu> {
   private static final ResourceLocation FRISKING_BACKGROUND = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/container/frisking.png");
   private static final int TICKS_TO_TAKE = 20;
   private static final int BAR_SEGMENTS = 10;
   boolean mouseHeld;
   int mouseHeldTicks;
   double mouseHeldX;
   double mouseHeldY;
   Slot heldSlot;

   public FriskingScreen(FriskingMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title);
      this.imageHeight = 256;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   private Component buildProgressBar(int ticks) {
      float pct = (float)ticks / 20.0F;
      int filled = Math.round(pct * 10.0F);
      int empty = 10 - filled;
      StringBuilder bar = new StringBuilder("*");

      for (int i = 0; i < filled; i++) {
         bar.append("|");
      }

      for (int i = 0; i < empty; i++) {
         bar.append("-");
      }

      bar.append("*");
      int percent = Math.min(100, Math.round(pct * 100.0F));
      String full = bar + " " + percent + "%";
      return Component.literal(full).withStyle(new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.YELLOW});
   }

   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(graphics, mouseX, mouseY, partialTick);
      super.render(graphics, mouseX, mouseY, partialTick);
      if (this.mouseHeld) {
         graphics.pose().pushPose();
         graphics.pose().translate(0.0F, 0.0F, 400.0F);
         Component barText = this.buildProgressBar(this.mouseHeldTicks);
         int barW = this.font.width(barText);
         graphics.drawString(this.font, barText, mouseX - barW / 2, mouseY - 18, 16777215, true);
         ScreenUtilities.renderLabel(
            this.minecraft,
            graphics,
            mouseX,
            mouseY + 6,
            List.of(Component.translatable("info.cuffed.frisk.taking").withStyle(ChatFormatting.WHITE)),
            16777215,
            true
         );
         graphics.pose().popPose();
      }

      if (!this.mouseHeld) {
         this.renderTooltip(graphics, mouseX, mouseY);
      }
   }

   protected void renderTooltip(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
      if (((FriskingMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         ItemStack itemstack = this.hoveredSlot.getItem();
         ArrayList<Component> tooltips = new ArrayList<>(this.getTooltipFromContainerItem(itemstack));
         if (this.hoveredSlot instanceof FriskingSlot) {
            tooltips.add(Component.translatable("info.cuffed.frisk.tooltip").withStyle(ChatFormatting.GRAY));
         }

         graphics.renderTooltip(this.font, tooltips, itemstack.getTooltipImage(), itemstack, mouseX, mouseY);
      }
   }

   protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      this.imageWidth = 176;
      this.imageHeight = 256;
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      graphics.blit(FRISKING_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
      Minecraft instance = this.minecraft;
      if (instance != null) {
         ClientLevel level = instance.level;
         if (level != null) {
            LocalPlayer player = instance.player;
            if (player != null) {
               Entity entity = null;

               for (LivingEntity p : level.players()) {
                  if (p.getDisplayName().getString().equals(this.title.getString())) {
                     entity = p;
                  }
               }

               if (entity == null) {
                  entity = player;
               }

               if (entity instanceof LivingEntity actualEntity) {
                  renderEntityInInventoryFollowsMouse(
                     graphics, i + 51, j + 71, 30, (float)(i + 51) - (float)mouseX, (float)(j + 75 - 50) - (float)mouseY, actualEntity
                  );
               }
            }
         }
      }
   }

   public static void renderEntityInInventoryFollowsMouse(GuiGraphics graphics, int x, int y, int z, float mouseX, float mouseY, LivingEntity entity) {
      float f = (float)Math.atan((double)(mouseX / 40.0F));
      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
      renderEntityInInventoryFollowsAngle(graphics, x, y, z, f, f1, entity);
   }

   public static void renderEntityInInventoryFollowsAngle(
      GuiGraphics graphics, int x, int y, int z, float angleXComponent, float angleYComponent, LivingEntity entity
   ) {
      Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf quaternionf1 = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0));
      quaternionf.mul(quaternionf1);
      float f2 = entity.yBodyRot;
      float f3 = entity.getYRot();
      float f4 = entity.getXRot();
      float f5 = entity.yHeadRotO;
      float f6 = entity.yHeadRot;
      entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
      entity.setYRot(180.0F + angleXComponent * 40.0F);
      entity.setXRot(-angleYComponent * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      renderEntityInInventory(graphics, x, y, z, quaternionf, quaternionf1, entity);
      entity.yBodyRot = f2;
      entity.setYRot(f3);
      entity.setXRot(f4);
      entity.yHeadRotO = f5;
      entity.yHeadRot = f6;
   }

   public static void renderEntityInInventory(
      GuiGraphics graphics, int x, int y, int z, Quaternionf rotationA, @Nullable Quaternionf rotationB, LivingEntity entity
   ) {
      graphics.pose().pushPose();
      graphics.pose().translate((double)x, (double)y, 50.0);
      graphics.pose().mulPose(new Matrix4f().scaling((float)z, (float)z, (float)(-z)));
      graphics.pose().mulPose(rotationA);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (rotationB != null) {
         rotationB.conjugate();
         entityrenderdispatcher.overrideCameraOrientation(rotationB);
      }

      entityrenderdispatcher.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880));
      graphics.flush();
      entityrenderdispatcher.setRenderShadow(true);
      graphics.pose().popPose();
      Lighting.setupFor3DItems();
   }

   protected void renderLabels(@Nonnull GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
      graphics.drawString(this.font, this.title, this.titleLabelX + 110 - this.font.width(this.title) / 2, this.titleLabelY + 20, 4210752, false);
   }

   private boolean isHovering(Slot slot, double mouseX, double mouseY) {
      return this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY);
   }

   @Nullable
   private Slot findSlot(double x, double y) {
      for (int i = 0; i < ((FriskingMenu)this.menu).slots.size(); i++) {
         Slot slot = (Slot)((FriskingMenu)this.menu).slots.get(i);
         if (this.isHovering(slot, x, y) && slot.isActive()) {
            return slot;
         }
      }

      return null;
   }

   protected void containerTick() {
      Minecraft mc = this.minecraft;
      if (mc != null && mc.level != null && mc.player != null) {
         String targetName = this.title.getString();
         boolean targetFound = false;
         boolean foundNear = false;

         for (Player p : mc.level.players()) {
            if (p.getDisplayName().getString().equals(targetName)) {
               targetFound = true;
               if (p == mc.player || mc.player.distanceToSqr(p) <= 4.0) {
                  foundNear = true;
               }
               break;
            }
         }

         if (targetFound && !foundNear) {
            mc.player.closeContainer();
            return;
         }
      }

      if (this.mouseHeld) {
         this.mouseHeldTicks++;
         if (this.mouseHeldTicks >= 20) {
            this.mouseClicked(this.mouseHeldX, this.mouseHeldY, 0);
            this.mouseHeld = false;
            this.mouseHeldX = 0.0;
            this.mouseHeldY = 0.0;
            this.mouseHeldTicks = 0;
            this.heldSlot = null;
         }
      }

      super.containerTick();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int keyCode) {
      if (this.mouseHeld) {
         super.mouseClicked(mouseX, mouseY, keyCode);
      }

      Key mouseKey = Type.MOUSE.getOrCreate(keyCode);
      Slot slot = this.findSlot(mouseX, mouseY);
      if (slot instanceof FriskingSlot && mouseKey.getValue() == 0 && slot != null && !slot.getItem().isEmpty()) {
         this.mouseHeld = true;
         this.mouseHeldX = mouseX;
         this.mouseHeldY = mouseY;
         this.mouseHeldTicks = 0;
         this.heldSlot = slot;
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, keyCode);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int keyCode) {
      this.mouseHeld = false;
      this.mouseHeldX = 0.0;
      this.mouseHeldY = 0.0;
      this.mouseHeldTicks = 0;
      this.heldSlot = null;
      return super.mouseReleased(mouseX, mouseY, keyCode);
   }
}
