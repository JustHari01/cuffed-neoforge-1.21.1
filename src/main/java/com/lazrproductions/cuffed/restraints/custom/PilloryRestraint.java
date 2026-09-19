package com.lazrproductions.cuffed.restraints.custom;

import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PilloryRestraint extends AbstractHeadRestraint {
   static final ResourceLocation CUFFED_WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture CHAIN_ICON = new ScreenTexture(CUFFED_WIDGETS, 44.0F, 24.0F, 16, 16, 192, 192);
   public static final ResourceLocation ID = ModRestraints.PILLORY.getId();
   public static final Item KEY = null;
   int lastBarIndex = 0;

   public PilloryRestraint() {
   }

   public PilloryRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      super(stack, player, captor);
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public String getActionBarLabel() {
      return "info.cuffed.restraints.pillory.action_bar";
   }

   @Override
   public String getName() {
      return "info.cuffed.restraints.pillory.name";
   }

   @Override
   public Item getItem() {
      // DeferredHolder#get() is only valid after the item registry has been bound.
      // The restraint class is constructed during registry setup, so resolving the
      // item in a static field makes NeoForge fail with an "unbound" registry value.
      return (Item)ModItems.PILLORY_ITEM.get();
   }

   @Override
   public Item getKeyItem() {
      return KEY;
   }

   @Override
   public SoundEvent getEquipSound() {
      return SoundEvents.WOOD_BREAK;
   }

   @Override
   public SoundEvent getUnequipSound() {
      return SoundEvents.WOOD_BREAK;
   }

   @Override
   public boolean AllowBreakingBlocks() {
      return false;
   }

   @Override
   public boolean AllowItemUse() {
      return false;
   }

   @Override
   public boolean AllowMovement() {
      return true;
   }

   @Override
   public boolean AllowJumping() {
      return true;
   }

   @Override
   public boolean AllowSprinting() {
      return true;
   }

   public boolean canBeBrokenOutOf() {
      return false;
   }

   @Override
   public boolean getLockpickable() {
      return false;
   }

   @Override
   public int getLockpickingProgressPerPick() {
      return 5;
   }

   @Override
   public int getLockpickingSpeedIncreasePerPick() {
      return 0;
   }

   @Override
   public ArmRestraintAnimationFlags getArmAnimationFlags() {
      return ArmRestraintAnimationFlags.NONE;
   }

   @Override
   public LegRestraintAnimationFlags getLegAnimationFlags() {
      return LegRestraintAnimationFlags.NONE;
   }

   @Override
   public void onTickServer(ServerPlayer player) {
      super.onTickServer(player);
   }

   @Override
   public void onTickClient(Player player) {
      super.onTickClient(player);
   }

   @Override
   public void onEquippedServer(ServerPlayer player, ServerPlayer captor) {
      super.onEquippedServer(player, captor);
   }

   @Override
   public void onEquippedClient(Player player, Player captor) {
      super.onEquippedClient(player, captor);
   }

   @Override
   public void onUnequippedServer(ServerPlayer player) {
      super.onUnequippedServer(player);
   }

   @Override
   public void onUnequippedClient(Player player) {
      super.onUnequippedClient(player);
   }

   @Override
   public void onLoginServer(ServerPlayer player) {
   }

   @Override
   public void onLoginClient(Player player) {
   }

   @Override
   public void onLogoutServer(ServerPlayer player) {
   }

   @Override
   public void onLogoutClient(Player player) {
   }

   @Override
   public void onDeathServer(ServerPlayer player) {
   }

   @Override
   public void onDeathClient(Player player) {
   }

   @Override
   public void onJumpServer(ServerPlayer player) {
   }

   @Override
   public void onJumpClient(Player player) {
   }

   @Override
   public float onLandServer(ServerPlayer player, float distance, float damageMultiplier) {
      return 1.0F;
   }

   @Override
   public void onLandClient(Player player, float distance, float damageMultiplier) {
   }

   @Override
   public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
      super.renderOverlay(player, graphics, partialTick, window);
      float f = 1.0F;
      graphics.setColor(f, f, f, 1.0F);
      int iconWidth = 28;
      int iconHeight = 28;
      int x = window.getGuiScaledWidth() / 2 - iconWidth / 2;
      int y = window.getGuiScaledHeight() / 2 - iconHeight - 100;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, iconWidth, iconHeight), CHAIN_ICON);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void onKeyInput(Player player, int keyCode, int action) {
      super.onKeyInput(player, keyCode, action);
   }

   @Override
   public void onMouseInput(Player player, int keyCode, int action) {
      super.onMouseInput(player, keyCode, action);
   }

   @Nonnull
   @OnlyIn(Dist.CLIENT)
   @Override
   public RestraintModelInterface getModelInterface() {
      return new PilloryRestraint.PilloryRestraintModelInterface();
   }

   @OnlyIn(Dist.CLIENT)
   public static class PilloryRestraintModelInterface extends RestraintModelInterface {
      static final Class<? extends HumanoidModel> MODEL_CLASS = null;
      static final ModelLayerLocation MODEL_LAYER = null;
      static final ResourceLocation MODEL_TEXTURE = null;

      @Override
      public Class<? extends HumanoidModel> getRenderedModel() {
         return MODEL_CLASS;
      }

      @Override
      public ModelLayerLocation getRenderedModelLayer() {
         return MODEL_LAYER;
      }

      @Override
      public ResourceLocation getRenderedModelTexture() {
         return MODEL_TEXTURE;
      }
   }
}
