package com.lazrproductions.cuffed.restraints.custom;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModModelLayers;
import com.lazrproductions.cuffed.init.ModRestraints;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.IBreakableRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.lazrproductions.cuffed.restraints.client.model.FuzzyHandcuffsModel;
import com.lazrproductions.lazrslib.client.screen.ScreenUtilities;
import com.lazrproductions.lazrslib.client.screen.base.BlitCoordinates;
import com.lazrproductions.lazrslib.client.screen.base.ScreenTexture;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.platform.Window;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class FuzzyHandcuffsRestraint extends AbstractArmRestraint implements IBreakableRestraint, IEnchantableRestraint {
   static final ResourceLocation CUFFED_WIDGETS = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/gui/widgets.png");
   static final ScreenTexture CHAIN_ICON = new ScreenTexture(CUFFED_WIDGETS, 92.0F, 24.0F, 16, 16, 192, 192);
   public static final ResourceLocation ID = ModRestraints.FUZZY_HANDCUFFS.getId();
   public static final Item ITEM = (Item)ModItems.FUZZY_HANDCUFFS.get();
   public static final Item KEY = (Item)ModItems.HANDCUFFS_KEY.get();
   public static final ArmRestraintAnimationFlags ARM_ANIMATION_FLAGS = ArmRestraintAnimationFlags.ARMS_TIED_BEHIND;
   int lastBarIndex = 0;
   private int durability = 100;
   float breakCooldown = 4.0F;
   int lastKeyPressed = -1;
   private ItemEnchantments enchantments = ItemEnchantments.EMPTY;

   public FuzzyHandcuffsRestraint() {
      this.enchantments = ItemEnchantments.EMPTY;
   }

   public FuzzyHandcuffsRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      super(stack, player, captor);
      this.durability = this.getMaxDurability() - stack.getDamageValue();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   @Override
   public String getActionBarLabel() {
      return "info.cuffed.restraints.fuzzy_handcuffs.action_bar";
   }

   @Override
   public String getName() {
      return "info.cuffed.restraints.fuzzy_handcuffs.name";
   }

   @Override
   public Item getItem() {
      return ITEM;
   }

   @Override
   public Item getKeyItem() {
      return KEY;
   }

   @Override
   public ArmRestraintAnimationFlags getArmAnimationFlags() {
      return ARM_ANIMATION_FLAGS;
   }

   @Override
   public LegRestraintAnimationFlags getLegAnimationFlags() {
      return LegRestraintAnimationFlags.NONE;
   }

   @Override
   public SoundEvent getEquipSound() {
      return ModSounds.HANDCUFFED;
   }

   @Override
   public SoundEvent getUnequipSound() {
      return (SoundEvent)SoundEvents.ARMOR_EQUIP_CHAIN.value();
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

   @Override
   public boolean getLockpickable() {
      return (Boolean)CuffedMod.SERVER_CONFIG.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKABLE.get();
   }

   @Override
   public int getLockpickingProgressPerPick() {
      return (Integer)CuffedMod.SERVER_CONFIG.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK.get();
   }

   @Override
   public int getLockpickingSpeedIncreasePerPick() {
      return (Integer)CuffedMod.SERVER_CONFIG.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK.get();
   }

   @Override
   public void onTickServer(ServerPlayer player) {
      super.onTickServer(player);
   }

   @Override
   public void onTickClient(Player player) {
      super.onTickClient(player);
      if (this.breakCooldown > 0.0F) {
         this.breakCooldown--;
      }
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
      float f = Mth.clamp(this.breakCooldown / 10.0F, 0.0F, 1.0F) + 1.0F;
      graphics.setColor(f, f, f, 1.0F);
      int iconWidth = 28;
      int iconHeight = 28;
      int x = window.getGuiScaledWidth() / 2 - iconWidth / 2;
      int y = window.getGuiScaledHeight() / 2 - iconHeight - 65;
      ScreenUtilities.drawTexture(graphics, new BlitCoordinates(x, y, iconWidth, iconHeight), CHAIN_ICON);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      float p = Mth.clamp((float)this.clientSidedDurability / (float)this.getMaxDurability(), 0.0F, 1.0F);
      ScreenUtilities.drawGenericProgressBar(graphics, new BlitCoordinates(x, y + iconHeight - 2, iconWidth, iconHeight), p);
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
      return new FuzzyHandcuffsRestraint.FuzzyHandcuffsRestraintModelInterface();
   }

   @Override
   public SoundEvent getBreakSound() {
      return SoundEvents.ITEM_BREAK;
   }

   @Override
   public boolean isKeyToAttemptBreak(int keyCode, Options options) {
      return keyCode == options.keyAttack.getKey().getValue() || keyCode == options.keyUse.getKey().getValue();
   }

   @Override
   public boolean requireAlternateKeysToAttemptBreak() {
      return true;
   }

   @Override
   public int getMaxDurability() {
      return ((Item)ModItems.FUZZY_HANDCUFFS.get()).getMaxDamage(((Item)ModItems.FUZZY_HANDCUFFS.get()).getDefaultInstance());
   }

   @Override
   public boolean dropItemOnBroken() {
      return (Boolean)CuffedMod.SERVER_CONFIG.FUZZY_HANDCUFFS_ON_ARMS_DROP_ITEM_WHEN_BROKEN.get();
   }

   @Override
   public boolean canBeBrokenOutOf() {
      return (Boolean)CuffedMod.SERVER_CONFIG.FUZZY_HANDCUFFS_ON_ARMS_CAN_BE_BROKEN_OUT_OF.get();
   }

   @Override
   public int getDurability() {
      return this.durability;
   }

   @Override
   public void attemptToBreak(Player player, int keyCode, int action, Options options) {
      if (this.breakCooldown <= 0.0F
         && this.canBeBrokenOutOf()
         && this.isKeyToAttemptBreak(keyCode, options)
         && (!this.requireAlternateKeysToAttemptBreak() || keyCode != this.lastKeyPressed)) {
         Random r = new Random();
         double chance = 0.5;
         double cooldownMultiplier = 1.0;
         Holder<Enchantment> unbreaking = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
         if (this instanceof IEnchantableRestraint && this.hasEnchantment(unbreaking)) {
            double d = (double)this.getEnchantmentLevel(unbreaking) / 3.0;
            chance = (MathUtilities.invert01(d / 3.0) * 0.7 + 0.3) * 0.5;
            cooldownMultiplier = 1.0 + d;
         }

         if (r.nextDouble() < chance) {
            this.lastKeyPressed = keyCode;
            player.playNotifySound(SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 1.0F, Mth.nextFloat(player.getRandom(), 0.9F, 1.1F));
            CuffedAPI.Networking.sendRestraintUtilityPacketToServer(this.getType(), 102, -1, false, 0.0, "");
            this.breakCooldown = (float)(r.nextInt(20) + Mth.floor(20.0 * cooldownMultiplier));
         }
      }
   }

   @Override
   public void setDurability(ServerPlayer player, int value) {
      if (value > this.getMaxDurability()) {
         value = this.getMaxDurability();
      }

      if (value < 0) {
         value = 0;
      }

      this.durability = value;
      CuffedAPI.Networking.sendRestraintUtilityPacketToClient(player, this.getType(), 101, this.durability, false, 0.0, "");
      if (this.durability <= 0) {
         this.onBrokenServer(player);
      }
   }

   @Override
   public void incrementDurability(ServerPlayer player, int value) {
      int newValue = this.getDurability() + value;
      this.setDurability(player, newValue);
   }

   @Override
   public void onBrokenServer(ServerPlayer player) {
      CuffedAPI.Networking.sendRestraintUtilityPacketToClient(player, this.getType(), 103, 0, false, 0.0, "");
      Random random = new Random();
      player.level().playSound(null, player.blockPosition(), this.getBreakSound(), SoundSource.PLAYERS, 0.8F, random.nextFloat() * 0.2F + 0.9F);
      ModStatistics.awardRestraintBroken(player, this);
      if (this.dropItemOnBroken()) {
         ItemStack stack = this.saveToItemStack(player.level().registryAccess());
         stack.setDamageValue(stack.getMaxDamage() - 1);
         ItemEntity e = new ItemEntity(player.level(), player.getX(), player.getY() + 0.6, player.getZ(), stack);
         e.setDefaultPickUpDelay();
         player.level().addFreshEntity(e);
      }

      IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
      if (this.getType() == RestraintType.Arm) {
         cap.setArmRestraintWithoutWarning(player, null);
      } else {
         cap.setLegRestraintWithoutWarning(player, null);
      }
   }

   @Override
   public void onBrokenClient(Player player) {
   }

   @Override
   public ItemEnchantments getEnchantments() {
      return this.enchantments;
   }

   @Override
   public void setEnchantments(ItemEnchantments tag) {
      this.enchantments = tag;
   }

   @OnlyIn(Dist.CLIENT)
   public static class FuzzyHandcuffsRestraintModelInterface extends RestraintModelInterface {
      static final Class<? extends HumanoidModel> MODEL_CLASS = FuzzyHandcuffsModel.class;
      static final ModelLayerLocation MODEL_LAYER = ModModelLayers.FUZZY_HANDCUFFS_LAYER;
      static final ResourceLocation MODEL_TEXTURE = ResourceLocation.fromNamespaceAndPath("cuffed", "textures/entity/fuzzy_handcuffs.png");

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
