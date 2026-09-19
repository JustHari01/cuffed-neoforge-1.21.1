package com.lazrproductions.cuffed.cap.base;

import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractHeadRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import com.mojang.blaze3d.platform.Window;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IRestrainableCapability {
   void copyFrom(CompoundTag var1, ServerLevel var2);

   CompoundTag serializeNBT(Provider var1);

   void deserializeNBT(Provider var1, CompoundTag var2);

   void tickServer(ServerPlayer var1);

   void tickClient(Player var1);

   void onKeyInput(Player var1, int var2, int var3);

   void onMouseInput(Player var1, int var2, int var3);

   void renderOverlay(Player var1, GuiGraphics var2, float var3, Window var4);

   boolean restraintsDisabledBreakingBlocks();

   boolean restraintsDisabledItemUse();

   boolean restraintsDisabledMovement();

   boolean restraintsDisabledJumping();

   int encodeRestraintDisabilities();

   boolean headRestrained();

   boolean armsRestrained();

   boolean legsRestrained();

   boolean isRestrained();

   boolean isRestrained(RestraintType var1);

   ResourceLocation getHeadRestraintId();

   ResourceLocation getArmRestraintId();

   ResourceLocation getLegRestraintId();

   ResourceLocation getRestraintId(RestraintType var1);

   @Nullable
   AbstractHeadRestraint getHeadRestraint();

   @Nullable
   AbstractArmRestraint getArmRestraint();

   @Nullable
   AbstractLegRestraint getLegRestraint();

   @Nullable
   AbstractRestraint getRestraint(RestraintType var1);

   void setHeadRestraintWithoutWarning(@Nonnull ServerPlayer var1, @Nullable AbstractHeadRestraint var2);

   void setArmRestraintWithoutWarning(@Nonnull ServerPlayer var1, @Nullable AbstractArmRestraint var2);

   void setLegRestraintWithoutWarning(@Nonnull ServerPlayer var1, @Nullable AbstractLegRestraint var2);

   void setRestraintWithoutWarning(@Nonnull ServerPlayer var1, @Nonnull AbstractRestraint var2, RestraintType var3);

   void startEscortingPlayer(@Nonnull ServerPlayer var1, @Nonnull ServerPlayer var2);

   void startGettingEscortedByPlayer(@Nonnull ServerPlayer var1);

   void stopEscortingPlayer();

   void stopGettingEscortedByPlayer();

   ServerPlayer getWhoImEscorting();

   ServerPlayer getMyEscort();

   boolean onInteractedByOther(ServerPlayer var1, ServerPlayer var2, double var3, ItemStack var5, InteractionHand var6, boolean var7);

   void onDeathServer(ServerPlayer var1);

   void onDeathClient(Player var1);

   void onLoginServer(ServerPlayer var1);

   void onLoginClient(Player var1);

   void onLogoutServer(ServerPlayer var1);

   void onLogoutClient(Player var1);

   float onLandServer(ServerPlayer var1, float var2, float var3);

   void onLandClient(Player var1, float var2, float var3);

   void onJumpServer(ServerPlayer var1);

   void onJumpClient(Player var1);

   boolean onTickRideServer(ServerPlayer var1, Entity var2);

   boolean onTickRideClient(Player var1, Entity var2);
}
