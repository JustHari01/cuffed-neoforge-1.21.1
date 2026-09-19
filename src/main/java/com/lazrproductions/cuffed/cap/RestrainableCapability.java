package com.lazrproductions.cuffed.cap;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.compat.BetterCombatCompat;
import com.lazrproductions.cuffed.compat.ElenaiDodge2Compat;
import com.lazrproductions.cuffed.compat.EpicFightCompat;
import com.lazrproductions.cuffed.compat.ParcoolCompat;
import com.lazrproductions.cuffed.compat.PlayerReviveCompat;
import com.lazrproductions.cuffed.effect.RestrainedEffectInstance;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.items.base.AbstractRestraintKeyItem;
import com.lazrproductions.cuffed.restraints.RestraintAPI;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.lazrproductions.lazrslib.common.math.MathUtilities;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class RestrainableCapability implements IRestrainableCapability, INBTSerializable<CompoundTag> {
   public AbstractArmRestraint armRestraint = null;
   public AbstractLegRestraint legRestraint = null;
   public AbstractHeadRestraint headRestraint = null;
   public ServerPlayer playerEscortingMe = null;
   public ServerPlayer whoImEscorting = null;
   public ServerPlayer playerFriskingMe = null;
   private boolean markedForSync = false;
   public int age = 0;

   @Override
   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag nbt = new CompoundTag();
      if (this.headRestraint != null) {
         nbt.put("HeadRestraint", this.headRestraint.serializeNBT(provider));
      }

      if (this.armRestraint != null) {
         nbt.put("ArmRestraint", this.armRestraint.serializeNBT(provider));
      }

      if (this.legRestraint != null) {
         nbt.put("LegRestraint", this.legRestraint.serializeNBT(provider));
      }

      return nbt;
   }

   @Override
   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      if (nbt.contains("HeadRestraint")) {
         this.headRestraint = (AbstractHeadRestraint)RestraintAPI.getRestraintFromTag(provider, nbt.getCompound("HeadRestraint"));
      } else {
         this.headRestraint = null;
      }

      if (nbt.contains("ArmRestraint")) {
         this.armRestraint = (AbstractArmRestraint)RestraintAPI.getRestraintFromTag(provider, nbt.getCompound("ArmRestraint"));
      } else {
         this.armRestraint = null;
      }

      if (nbt.contains("LegRestraint")) {
         this.legRestraint = (AbstractLegRestraint)RestraintAPI.getRestraintFromTag(provider, nbt.getCompound("LegRestraint"));
      } else {
         this.legRestraint = null;
      }

      this.markedForSync = true;
   }

   @Override
   public void tickServer(ServerPlayer player) {
      if (this.headRestraint != null) {
         this.headRestraint.onTickServer(player);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onTickServer(player);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onTickServer(player);
      }

      int encoded = this.encodeRestraintDisabilities();
      if (this.isRestrained()) {
         if (!player.hasEffect(ModEffects.RESTRAINED_EFFECT)) {
            MobEffectInstance inst = new MobEffectInstance(ModEffects.RESTRAINED_EFFECT, -1, encoded);
            player.addEffect(inst);
         } else {
            MobEffectInstance e = player.getEffect(ModEffects.RESTRAINED_EFFECT);
            if (e != null && e.getAmplifier() != encoded) {
               player.removeEffect(ModEffects.RESTRAINED_EFFECT);
               MobEffectInstance inst = new MobEffectInstance(ModEffects.RESTRAINED_EFFECT, -1, encoded);
               player.addEffect(inst);
            }
         }
      } else if (player.hasEffect(ModEffects.RESTRAINED_EFFECT)) {
         player.removeEffect(ModEffects.RESTRAINED_EFFECT);
      }

      if (this.playerEscortingMe != null) {
         this.playerEscortingMe.sendSystemMessage(Component.translatable("info.cuffed.escorting.giving").append(player.getDisplayName()), true);
         player.sendSystemMessage(Component.translatable("info.cuffed.escorting.getting").append(this.playerEscortingMe.getDisplayName()), true);
         Vec3 escortPivot = MathUtilities.GetPositionFromTowardsRotationInDegrees(
            this.playerEscortingMe.position(), (double)(this.playerEscortingMe.getYRot() + 90.0F), 0.0, 0.45F
         );
         Vec3 escortTarget = MathUtilities.GetPositionFromTowardsRotationInDegrees(escortPivot, (double)this.playerEscortingMe.getYRot(), 0.0, 0.9F);
         player.teleportTo(
            (ServerLevel)this.playerEscortingMe.level(),
            escortTarget.x(),
            escortTarget.y(),
            escortTarget.z(),
            this.playerEscortingMe.getYRot(),
            player.getXRot()
         );
         if (!this.isRestrained()) {
            CuffedAPI.Capabilities.getRestrainableCapability(this.playerEscortingMe).stopEscortingPlayer();
         } else if (this.playerEscortingMe != null && !this.playerEscortingMe.isRemoved()) {
            if (!(this.playerEscortingMe.distanceTo(player) > 3.5F) && !this.playerEscortingMe.isCrouching()) {
               if (this.playerEscortingMe != null && this.playerEscortingMe.isRemoved() || player.isRemoved()) {
                  CuffedAPI.Capabilities.getRestrainableCapability(this.playerEscortingMe).stopEscortingPlayer();
               }
            } else {
               this.playerEscortingMe.sendSystemMessage(Component.translatable("info.cuffed.escorting.giving.cancel").append(player.getDisplayName()), true);
               player.sendSystemMessage(
                  Component.translatable("info.cuffed.escorting.getting.cancel", new Object[]{this.playerEscortingMe.getDisplayName()}), true
               );
               CuffedAPI.Capabilities.getRestrainableCapability(this.playerEscortingMe).stopEscortingPlayer();
            }
         } else {
            this.playerEscortingMe = null;
         }
      }

      if (this.whoImEscorting == null || this.whoImEscorting.isRemoved()) {
         this.whoImEscorting = null;
      }

      if (this.markedForSync) {
         CuffedAPI.Networking.sendRestraintSyncPacket(player);
         this.markedForSync = false;
      }

      this.age++;
   }

   @Override
   public void copyFrom(CompoundTag tag, ServerLevel level) {
      this.deserializeNBT(level.registryAccess(), tag);
   }

   @Override
   public boolean onInteractedByOther(
      ServerPlayer player, ServerPlayer other, double interactionHeight, ItemStack stack, InteractionHand hand, boolean avoidEscort
   ) {
      if (RestraintAPI.isRestraintItem(stack)) {
         if (interactionHeight > 1.5
            && RestraintAPI.getRestraintFromStack(stack, RestraintType.Head, player, other) instanceof AbstractHeadRestraint a
            && this.TryEquipRestraint(player, other, a)) {
            ModStatistics.awardRestraintItemUsed(other, stack);
            stack.shrink(1);
            return true;
         }

         if (interactionHeight > 0.33F
            && interactionHeight <= 1.5
            && RestraintAPI.getRestraintFromStack(stack, RestraintType.Arm, player, other) instanceof AbstractArmRestraint a
            && this.TryEquipRestraint(player, other, a)) {
            ModStatistics.awardRestraintItemUsed(other, stack);
            stack.shrink(1);
            return true;
         }

         if (interactionHeight <= 0.33F
            && RestraintAPI.getRestraintFromStack(stack, RestraintType.Leg, player, other) instanceof AbstractLegRestraint l
            && this.TryEquipRestraint(player, other, l)) {
            ModStatistics.awardRestraintItemUsed(other, stack);
            stack.shrink(1);
            return true;
         }
      } else if (stack.getItem() instanceof AbstractRestraintKeyItem) {
         if (interactionHeight > 1.5) {
            if (this.headRestraint != null
               && (this.headRestraint.getKeyItem() == null || this.headRestraint.getKeyItem() == stack.getItem())
               && this.TryUnequipRestraint(player, other, RestraintType.Head)) {
               other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
               return true;
            }
         } else if (interactionHeight > 0.33F && interactionHeight <= 1.5) {
            if (this.armRestraint != null
               && (this.armRestraint.getKeyItem() == null || this.armRestraint.getKeyItem() == stack.getItem())
               && this.TryUnequipRestraint(player, other, RestraintType.Arm)) {
               other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
               return true;
            }
         } else if (interactionHeight <= 0.33F
            && this.legRestraint != null
            && (this.legRestraint.getKeyItem() == null || this.legRestraint.getKeyItem() == stack.getItem())
            && this.TryUnequipRestraint(player, other, RestraintType.Leg)) {
            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
            return true;
         }
      } else if (stack.is((Item)ModItems.LOCKPICK.get())) {
         int lockpickType = -1;
         if (interactionHeight > 1.5 && this.headRestrained() && this.headRestraint.getLockpickable()) {
            lockpickType = RestraintType.Head.toInteger();
         } else if (interactionHeight > 0.33F && interactionHeight <= 1.5 && this.armsRestrained() && this.armRestraint.getLockpickable()) {
            lockpickType = RestraintType.Arm.toInteger();
         } else if (interactionHeight <= 0.33F && this.legsRestrained() && this.legRestraint.getLockpickable()) {
            lockpickType = RestraintType.Leg.toInteger();
         }

         if (lockpickType > -1) {
            int speedIncreasePerPick = lockpickType == RestraintType.Leg.toInteger()
               ? this.getLegRestraint().getLockpickingSpeedIncreasePerPick()
               : (
                  lockpickType == RestraintType.Arm.toInteger()
                     ? this.getArmRestraint().getLockpickingSpeedIncreasePerPick()
                     : this.getHeadRestraint().getLockpickingSpeedIncreasePerPick()
               );
            int progressPerPick = lockpickType == RestraintType.Leg.toInteger()
               ? this.getLegRestraint().getLockpickingProgressPerPick()
               : (
                  lockpickType == RestraintType.Arm.toInteger()
                     ? this.getArmRestraint().getLockpickingProgressPerPick()
                     : this.getHeadRestraint().getLockpickingProgressPerPick()
               );
            CuffedAPI.Networking.sendLockpickBeginPickingRestraintPacketToClient(
               other, player.getUUID().toString(), lockpickType, speedIncreasePerPick, progressPerPick
            );
            return true;
         }
      } else if (stack.isEmpty() && other.isCrouching()) {
         if (interactionHeight > 1.5) {
            if (this.headRestraint != null && this.headRestraint.getKeyItem() == null && this.TryUnequipRestraint(player, other, RestraintType.Head)) {
               other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
               return true;
            }
         } else if (interactionHeight > 0.33F && interactionHeight <= 1.5) {
            if (this.armRestraint != null && this.armRestraint.getKeyItem() == null && this.TryUnequipRestraint(player, other, RestraintType.Arm)) {
               other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
               return true;
            }
         } else if (interactionHeight <= 0.33F
            && this.legRestraint != null
            && this.legRestraint.getKeyItem() == null
            && this.TryUnequipRestraint(player, other, RestraintType.Leg)) {
            other.awardStat(Stats.ITEM_USED.get(stack.getItem()), 1);
            return true;
         }
      } else if (stack.isEmpty() && !other.isCrouching() && this.isRestrained() && !avoidEscort) {
         CuffedAPI.Capabilities.getRestrainableCapability(other).startEscortingPlayer(other, player);
         return true;
      }

      return false;
   }

   @Override
   public boolean restraintsDisabledBreakingBlocks() {
      boolean v = false;
      if (this.armRestraint != null && !this.armRestraint.AllowBreakingBlocks()) {
         v = true;
      }

      if (this.legRestraint != null && !this.legRestraint.AllowBreakingBlocks()) {
         v = true;
      }

      if (this.headRestraint != null && !this.headRestraint.AllowBreakingBlocks()) {
         v = true;
      }

      return v;
   }

   @Override
   public boolean restraintsDisabledItemUse() {
      boolean v = false;
      if (this.armRestraint != null && !this.armRestraint.AllowItemUse()) {
         v = true;
      }

      if (this.legRestraint != null && !this.legRestraint.AllowItemUse()) {
         v = true;
      }

      if (this.headRestraint != null && !this.headRestraint.AllowItemUse()) {
         v = true;
      }

      return v;
   }

   @Override
   public boolean restraintsDisabledMovement() {
      boolean v = false;
      if (this.armRestraint != null && !this.armRestraint.AllowMovement()) {
         v = true;
      }

      if (this.legRestraint != null && !this.legRestraint.AllowMovement()) {
         v = true;
      }

      if (this.headRestraint != null && !this.headRestraint.AllowMovement()) {
         v = true;
      }

      return v;
   }

   @Override
   public boolean restraintsDisabledJumping() {
      boolean v = false;
      if (this.armRestraint != null && !this.armRestraint.AllowJumping()) {
         v = true;
      }

      if (this.legRestraint != null && !this.legRestraint.AllowJumping()) {
         v = true;
      }

      if (this.headRestraint != null && !this.headRestraint.AllowJumping()) {
         v = true;
      }

      return v;
   }

   @Override
   public int encodeRestraintDisabilities() {
      boolean noMining = false;
      boolean noItemUse = false;
      boolean noMovement = false;
      boolean noJumping = false;
      if (this.armRestraint != null) {
         if (!this.armRestraint.AllowBreakingBlocks()) {
            noMining = true;
         }

         if (!this.armRestraint.AllowItemUse()) {
            noItemUse = true;
         }

         if (!this.armRestraint.AllowMovement()) {
            noMovement = true;
         }

         if (!this.armRestraint.AllowJumping()) {
            noJumping = true;
         }
      }

      if (this.legRestraint != null) {
         if (!this.legRestraint.AllowBreakingBlocks()) {
            noMining = true;
         }

         if (!this.legRestraint.AllowItemUse()) {
            noItemUse = true;
         }

         if (!this.legRestraint.AllowMovement()) {
            noMovement = true;
         }

         if (!this.legRestraint.AllowJumping()) {
            noJumping = true;
         }
      }

      if (this.headRestraint != null) {
         if (!this.headRestraint.AllowBreakingBlocks()) {
            noMining = true;
         }

         if (!this.headRestraint.AllowItemUse()) {
            noItemUse = true;
         }

         if (!this.headRestraint.AllowMovement()) {
            noMovement = true;
         }

         if (!this.headRestraint.AllowJumping()) {
            noJumping = true;
         }
      }

      return RestrainedEffectInstance.encodeRestraintProperties(noMining, noItemUse, noMovement, noJumping);
   }

   @Override
   public boolean armsRestrained() {
      return this.armRestraint != null;
   }

   @Override
   public boolean legsRestrained() {
      return this.legRestraint != null;
   }

   @Override
   public boolean headRestrained() {
      return this.headRestraint != null;
   }

   @Override
   public boolean isRestrained() {
      return this.armsRestrained() || this.legsRestrained() || this.headRestrained();
   }

   @Override
   public boolean isRestrained(RestraintType type) {
      if (type == RestraintType.Arm) {
         return this.armsRestrained();
      } else {
         return type == RestraintType.Leg ? this.legsRestrained() : this.headRestrained();
      }
   }

   @Override
   public ResourceLocation getArmRestraintId() {
      return this.armRestraint != null ? this.armRestraint.getId() : ResourceLocation.fromNamespaceAndPath("minecraft", "air");
   }

   @Override
   public ResourceLocation getLegRestraintId() {
      return this.legRestraint != null ? this.legRestraint.getId() : ResourceLocation.fromNamespaceAndPath("minecraft", "air");
   }

   @Override
   public ResourceLocation getHeadRestraintId() {
      return this.headRestraint != null ? this.headRestraint.getId() : ResourceLocation.fromNamespaceAndPath("minecraft", "air");
   }

   @Override
   public ResourceLocation getRestraintId(RestraintType type) {
      if (type == RestraintType.Arm) {
         return this.getArmRestraintId();
      } else {
         return type == RestraintType.Head ? this.getHeadRestraintId() : this.getLegRestraintId();
      }
   }

   @Override
   public AbstractArmRestraint getArmRestraint() {
      return this.armRestraint;
   }

   @Override
   public AbstractLegRestraint getLegRestraint() {
      return this.legRestraint;
   }

   @Override
   public AbstractHeadRestraint getHeadRestraint() {
      return this.headRestraint;
   }

   @Override
   public AbstractRestraint getRestraint(RestraintType type) {
      if (type == RestraintType.Arm) {
         return this.getArmRestraint();
      } else {
         return (AbstractRestraint)(type == RestraintType.Head ? this.getHeadRestraint() : this.getLegRestraint());
      }
   }

   @Override
   public void setArmRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractArmRestraint newRestraint) {
      this.armRestraint = newRestraint;
      CuffedAPI.Networking.sendRestraintSyncPacket(player);
   }

   @Override
   public void setLegRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractLegRestraint newRestraint) {
      this.legRestraint = newRestraint;
      CuffedAPI.Networking.sendRestraintSyncPacket(player);
   }

   @Override
   public void setHeadRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nullable AbstractHeadRestraint newRestraint) {
      this.headRestraint = newRestraint;
      CuffedAPI.Networking.sendRestraintSyncPacket(player);
   }

   @Override
   public void setRestraintWithoutWarning(@Nonnull ServerPlayer player, @Nonnull AbstractRestraint newValue, RestraintType type) {
      if (type == RestraintType.Arm) {
         if (newValue instanceof AbstractArmRestraint arm) {
            this.armRestraint = arm;
         }
      } else if (type == RestraintType.Head) {
         if (newValue instanceof AbstractHeadRestraint head) {
            this.headRestraint = head;
         }
      } else if (type == RestraintType.Leg && newValue instanceof AbstractLegRestraint leg) {
         this.legRestraint = leg;
      }

      CuffedAPI.Networking.sendRestraintSyncPacket(player);
   }

   public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractArmRestraint restraint) {
      if (this.armRestraint == null) {
         if (!(Boolean)CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() || this.isRestrained()) {
            this.EquipRestraint(player, captor, restraint);
            return true;
         }

         if (!CuffedMod.PlayerReviveInstalled) {
            if (player.getHealth() / player.getMaxHealth() <= 0.3F) {
               player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
               this.EquipRestraint(player, captor, restraint);
               return true;
            }
         } else if (PlayerReviveCompat.IsBleedingOut(player)) {
            PlayerReviveCompat.Revive(player);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
            this.EquipRestraint(player, captor, restraint);
            return true;
         }
      }

      return false;
   }

   public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractLegRestraint restraint) {
      if (this.legRestraint == null) {
         if (!(Boolean)CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() || this.isRestrained()) {
            this.EquipRestraint(player, captor, restraint);
            return true;
         }

         if (!CuffedMod.PlayerReviveInstalled) {
            if (player.getHealth() / player.getMaxHealth() <= 0.3F) {
               player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
               this.EquipRestraint(player, captor, restraint);
               return true;
            }
         } else if (PlayerReviveCompat.IsBleedingOut(player)) {
            PlayerReviveCompat.Revive(player);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
            this.EquipRestraint(player, captor, restraint);
            return true;
         }
      }

      return false;
   }

   public boolean TryEquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractHeadRestraint restraint) {
      if (this.headRestraint == null) {
         if (!(Boolean)CuffedMod.SERVER_CONFIG.REQUIRE_LOW_HEALTH_TO_RESTRAIN.get() || this.isRestrained()) {
            this.EquipRestraint(player, captor, restraint);
            return true;
         }

         if (!CuffedMod.PlayerReviveInstalled) {
            if (player.getHealth() / player.getMaxHealth() <= 0.3F) {
               player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
               this.EquipRestraint(player, captor, restraint);
               return true;
            }
         } else if (PlayerReviveCompat.IsBleedingOut(player)) {
            PlayerReviveCompat.Revive(player);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
            this.EquipRestraint(player, captor, restraint);
            return true;
         }
      }

      return false;
   }

   public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractArmRestraint restraint) {
      AbstractLegRestraint oldRestraint = this.legRestraint;
      this.armRestraint = restraint;
      this.armRestraint.onEquippedServer(player, captor);
      CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Arm, this.armRestraint, oldRestraint);
   }

   public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractLegRestraint restraint) {
      AbstractLegRestraint oldRestraint = this.legRestraint;
      this.legRestraint = restraint;
      this.legRestraint.onEquippedServer(player, captor);
      CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Leg, this.legRestraint, oldRestraint);
   }

   public void EquipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer captor, AbstractHeadRestraint restraint) {
      AbstractHeadRestraint oldRestraint = this.headRestraint;
      this.headRestraint = restraint;
      this.headRestraint.onEquippedServer(player, captor);
      CuffedAPI.Networking.sendRestraintEquipPacket(player, captor, RestraintType.Head, this.headRestraint, oldRestraint);
   }

   public boolean TryUnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser, RestraintType type) {
      if ((type != RestraintType.Arm || this.armRestraint == null)
         && (type != RestraintType.Leg || this.legRestraint == null)
         && (type != RestraintType.Head || this.headRestraint == null)) {
         return false;
      } else {
         this.UnequipRestraint(player, releaser, type);
         return true;
      }
   }

   public void UnequipRestraint(@Nonnull ServerPlayer player, @Nullable ServerPlayer releaser, RestraintType type) {
      AbstractRestraint oldRestraint = null;
      if (type == RestraintType.Arm) {
         oldRestraint = this.armRestraint;
      } else if (type == RestraintType.Leg) {
         oldRestraint = this.legRestraint;
      } else if (type == RestraintType.Head) {
         oldRestraint = this.headRestraint;
      }

      if (oldRestraint != null) {
         ItemStack stack = oldRestraint.saveToItemStack(player.registryAccess());
         if (releaser == null || !releaser.addItem(stack)) {
            ItemEntity e = new ItemEntity(player.level(), player.getX(), player.getY() + 0.6, player.getZ(), stack);
            e.setDefaultPickUpDelay();
            player.level().addFreshEntity(e);
         }

         oldRestraint.onUnequippedServer(player);
      }

      if (type == RestraintType.Arm) {
         this.armRestraint = null;
      } else if (type == RestraintType.Leg) {
         this.legRestraint = null;
      } else if (type == RestraintType.Head) {
         this.headRestraint = null;
      }

      CuffedAPI.Networking.sendRestraintEquipPacket(player, releaser, type, null, oldRestraint);
   }

   public void beginSkillcheck() {
   }

   @Override
   public void startEscortingPlayer(@Nonnull ServerPlayer self, @Nonnull ServerPlayer playerToEscort) {
      playerToEscort.removeVehicle();
      CuffedAPI.Capabilities.getRestrainableCapability(playerToEscort).startGettingEscortedByPlayer(self);
      this.whoImEscorting = playerToEscort;
   }

   @Override
   public void startGettingEscortedByPlayer(@Nonnull ServerPlayer other) {
      this.playerEscortingMe = other;
   }

   @Override
   public void stopEscortingPlayer() {
      CuffedAPI.Capabilities.getRestrainableCapability(this.whoImEscorting).stopGettingEscortedByPlayer();
      this.whoImEscorting = null;
   }

   @Override
   public void stopGettingEscortedByPlayer() {
      this.playerEscortingMe = null;
   }

   @Override
   public ServerPlayer getWhoImEscorting() {
      return this.whoImEscorting;
   }

   @Override
   public ServerPlayer getMyEscort() {
      return this.playerEscortingMe;
   }

   @Override
   public void tickClient(Player player) {
      if (this.isRestrained()) {
         if (CuffedMod.BetterCombatInstalled) {
            BetterCombatCompat.Disable();
         }

         if (CuffedMod.EpicFightInstalled) {
            EpicFightCompat.Disable(player);
         }

         if (CuffedMod.ParcoolInstalled) {
            ParcoolCompat.Disable();
         }

         if (CuffedMod.ElenaiDodge2Installed) {
            ElenaiDodge2Compat.Disable();
         }
      } else {
         if (CuffedMod.BetterCombatInstalled) {
            BetterCombatCompat.Reset();
         }

         if (CuffedMod.EpicFightInstalled) {
            EpicFightCompat.Reset(player);
         }

         if (CuffedMod.ParcoolInstalled) {
            ParcoolCompat.Reset();
         }

         if (CuffedMod.ElenaiDodge2Installed) {
            ElenaiDodge2Compat.Reset();
         }
      }

      if (this.headRestraint != null) {
         this.headRestraint.onTickClient(player);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onTickClient(player);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onTickClient(player);
      }
   }

   @Override
   public void onKeyInput(Player player, int keyCode, int action) {
      if (this.headRestraint != null) {
         this.headRestraint.onKeyInput(player, keyCode, action);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onKeyInput(player, keyCode, action);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onKeyInput(player, keyCode, action);
      }
   }

   @Override
   public void onMouseInput(Player player, int keyCode, int action) {
      if (this.headRestraint != null) {
         this.headRestraint.onMouseInput(player, keyCode, action);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onMouseInput(player, keyCode, action);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onMouseInput(player, keyCode, action);
      }
   }

   @Override
   public void renderOverlay(Player player, GuiGraphics graphics, float partialTick, Window window) {
      if (this.headRestraint != null) {
         this.headRestraint.renderOverlay(player, graphics, partialTick, window);
      }

      if (this.armRestraint != null) {
         this.armRestraint.renderOverlay(player, graphics, partialTick, window);
      }

      if (this.legRestraint != null) {
         this.legRestraint.renderOverlay(player, graphics, partialTick, window);
      }
   }

   public ArrayList<Integer> gatherBlockedInputs() {
      ArrayList<Integer> b = new ArrayList<>();
      if (this.headRestraint != null) {
         b.addAll(this.headRestraint.getBlockedKeyCodes());
      }

      if (this.armRestraint != null) {
         b.addAll(this.armRestraint.getBlockedKeyCodes());
      }

      if (this.legRestraint != null) {
         b.addAll(this.legRestraint.getBlockedKeyCodes());
      }

      return b;
   }

   @Override
   public void onLoginServer(ServerPlayer player) {
      if (player.hasEffect(ModEffects.RESTRAINED_EFFECT)) {
         player.removeEffect(ModEffects.RESTRAINED_EFFECT);
      }

      if (this.isRestrained()) {
         int encoded = this.encodeRestraintDisabilities();
         MobEffectInstance inst = new MobEffectInstance(ModEffects.RESTRAINED_EFFECT, -1, encoded);
         player.addEffect(inst);
      }
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
      Registry<Enchantment> enchantmentRegistry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
      Reference<Enchantment> bindingCurseHolder = enchantmentRegistry.getHolderOrThrow(Enchantments.BINDING_CURSE);
      if (this.headRestraint != null) {
         this.headRestraint.onDeathServer(player);
         boolean shouldUnequip = true;
         if (this.headRestraint instanceof IEnchantableRestraint e && e.hasEnchantment(bindingCurseHolder)) {
            shouldUnequip = false;
         }

         if (shouldUnequip) {
            this.TryUnequipRestraint(player, null, RestraintType.Head);
         }
      }

      if (this.armRestraint != null) {
         this.armRestraint.onDeathServer(player);
         boolean shouldUnequipx = true;
         if (this.armRestraint instanceof IEnchantableRestraint e && e.hasEnchantment(bindingCurseHolder)) {
            shouldUnequipx = false;
         }

         if (shouldUnequipx) {
            this.TryUnequipRestraint(player, null, RestraintType.Arm);
         }
      }

      if (this.legRestraint != null) {
         this.legRestraint.onDeathServer(player);
         boolean shouldUnequipxx = true;
         if (this.legRestraint instanceof IEnchantableRestraint e && e.hasEnchantment(bindingCurseHolder)) {
            shouldUnequipxx = false;
         }

         if (shouldUnequipxx) {
            this.TryUnequipRestraint(player, null, RestraintType.Leg);
         }
      }
   }

   @Override
   public void onDeathClient(Player player) {
      if (this.headRestraint != null) {
         this.headRestraint.onDeathClient(player);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onDeathClient(player);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onDeathClient(player);
      }
   }

   @Override
   public float onLandServer(ServerPlayer player, float distance, float damageMultiplier) {
      float mult = damageMultiplier;
      if (this.headRestraint != null) {
         mult = damageMultiplier * this.headRestraint.onLandServer(player, distance, damageMultiplier);
      }

      if (this.armRestraint != null) {
         mult *= this.armRestraint.onLandServer(player, distance, damageMultiplier);
      }

      if (this.legRestraint != null) {
         mult *= this.legRestraint.onLandServer(player, distance, damageMultiplier);
      }

      return mult;
   }

   @Override
   public void onLandClient(Player player, float distance, float damageMultiplier) {
      if (this.headRestraint != null) {
         this.headRestraint.onLandClient(player, distance, damageMultiplier);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onLandClient(player, distance, damageMultiplier);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onLandClient(player, distance, damageMultiplier);
      }
   }

   @Override
   public void onJumpServer(ServerPlayer player) {
      if (this.headRestraint != null) {
         this.headRestraint.onJumpServer(player);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onJumpServer(player);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onJumpServer(player);
      }
   }

   @Override
   public void onJumpClient(Player player) {
      if (this.headRestraint != null) {
         this.headRestraint.onJumpClient(player);
      }

      if (this.armRestraint != null) {
         this.armRestraint.onJumpClient(player);
      }

      if (this.legRestraint != null) {
         this.legRestraint.onJumpClient(player);
      }
   }

   @Override
   public boolean onTickRideServer(ServerPlayer player, Entity vehicle) {
      vehicle.setDeltaMovement(Vec3.ZERO);
      return true;
   }

   @Override
   public boolean onTickRideClient(Player player, Entity vehicle) {
      vehicle.setDeltaMovement(Vec3.ZERO);
      return true;
   }
}
