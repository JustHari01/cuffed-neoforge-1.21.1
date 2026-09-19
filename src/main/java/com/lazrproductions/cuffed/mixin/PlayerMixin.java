package com.lazrproductions.cuffed.mixin;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.base.DetentionBlock;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.entity.base.IPrivacyOperand;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.init.ModEffects;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class PlayerMixin extends LivingEntity implements IRestrainableEntity, IDetainableEntity, INicknamable, IPrivacyOperand {
   private static final EntityDataAccessor<Boolean> DATA_HAS_NICKNAME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Component> DATA_NICKNAME = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPONENT);
   private static final String NICKNAME_TAG = "Nickname";
   private static final EntityDataAccessor<Integer> DATA_RESTRAINT_CODE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<String> DATA_ARM_RESTRAINT_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<String> DATA_LEG_RESTRAINT_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<String> DATA_HEAD_RESTRAINT_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Boolean> DATA_ARM_ENCHANTED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_LEG_ENCHANTED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_HEAD_ENCHANTED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_DETAINED = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<BlockPos> DATA_DETAINED_TO_BLOCK = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Float> DATA_DETAINED_ROTATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Vector3f> DATA_DETAINED_POSITION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.VECTOR3);
   private static final String DETAINED_TAG = "Detained";
   private static final String DETAINED_ID_TAG = "Id";
   private static final String DETAINED_ROTATION_TAG = "Rotation";
   private static final String DETAINED_TO_BLOCK_TAG = "Block";
   private static final String DETAINED_POSITION_X_TAG = "X";
   private static final String DETAINED_POSITION_Y_TAG = "Y";
   private static final String DETAINED_POSITION_Z_TAG = "Z";
   float pilloryBreakOutProgress = 0.0F;
   boolean wasCrouching;
   boolean hasProcessedSwing = false;
   final HashMap<String, IPrivacyOperand.PrivacyRestriction> privacyRestrictions = new HashMap<>();

   protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
      super(p_20966_, p_20967_);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"defineSynchedData"}
   )
   protected void defineSynchedData(Builder builder, CallbackInfo callback) {
      builder.define(DATA_HAS_NICKNAME, false);
      builder.define(DATA_NICKNAME, Component.empty());
      builder.define(DATA_RESTRAINT_CODE, 0);
      builder.define(DATA_ARM_RESTRAINT_ID, "");
      builder.define(DATA_LEG_RESTRAINT_ID, "");
      builder.define(DATA_HEAD_RESTRAINT_ID, "");
      builder.define(DATA_ARM_ENCHANTED, false);
      builder.define(DATA_LEG_ENCHANTED, false);
      builder.define(DATA_HEAD_ENCHANTED, false);
      builder.define(DATA_DETAINED, -1);
      builder.define(DATA_DETAINED_ROTATION, 0.0F);
      builder.define(DATA_DETAINED_TO_BLOCK, BlockPos.ZERO);
      builder.define(DATA_DETAINED_POSITION, new Vector3f());
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getName"},
      cancellable = true
   )
   public void getName(CallbackInfoReturnable<Component> callback) {
      if ((Boolean)this.entityData.get(DATA_HAS_NICKNAME)) {
         Component n = this.getNickname();
         if (n != null) {
            callback.setReturnValue(n);
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getDisplayName"},
      cancellable = true
   )
   public void getDisplayName(CallbackInfoReturnable<Component> callback) {
      if ((Boolean)this.entityData.get(DATA_HAS_NICKNAME)) {
         Component n = this.getNickname();
         if (n != null) {
            callback.setReturnValue(n);
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   public void tick(CallbackInfo callback) {
      if (!this.level().isClientSide()) {
         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability((Player)(Object)this);
         if (cap != null) {
            boolean var10001;
            label96: {
               cap.tickServer((ServerPlayer)(Object)this);
               this.setArmRestraintId(cap.getArmRestraintId());
               this.setLegRestraintId(cap.getLegRestraintId());
               this.setHeadRestraintId(cap.getHeadRestraintId());
               if (cap.getArmRestraint() instanceof IEnchantableRestraint e && e.getEnchantments() != null && e.getEnchantments().size() > 0) {
                  var10001 = true;
                  break label96;
               }

               var10001 = false;
            }

            label90: {
               this.setArmsEnchanted(var10001);
               if (cap.getLegRestraint() instanceof IEnchantableRestraint e && e.getEnchantments() != null && e.getEnchantments().size() > 0) {
                  var10001 = true;
                  break label90;
               }

               var10001 = false;
            }

            label84: {
               this.setLegsEnchanted(var10001);
               if (cap.getHeadRestraint() instanceof IEnchantableRestraint e && e.getEnchantments() != null && e.getEnchantments().size() > 0) {
                  var10001 = true;
                  break label84;
               }

               var10001 = false;
            }

            this.setHeadEnchanted(var10001);
         }

         MobEffectInstance eff = this.getEffect(ModEffects.RESTRAINED_EFFECT);
         if (eff == null) {
            this.setRestraintCode(0);
         } else {
            this.setRestraintCode(eff.getAmplifier());
         }

         if (this.getDetained() > -1) {
            this.setYBodyRot(this.getDetainedRotation());
            this.setYRot(this.getDetainedRotation());
            this.teleportTo((double)this.getDetainedPosition().x(), (double)this.getDetainedPosition().y(), (double)this.getDetainedPosition().z());
            if (!this.isAlive()) {
               this.undetain();
            } else {
               if (!this.wasCrouching && this.isCrouching()) {
                  this.pilloryBreakOutProgress++;
                  this.wasCrouching = true;
               } else if (this.wasCrouching && !this.isCrouching()) {
                  this.pilloryBreakOutProgress++;
                  this.wasCrouching = false;
               }

               BlockPos pos = (BlockPos)this.entityData.get(DATA_DETAINED_TO_BLOCK);
               BlockState state = this.getBlockDetainedTo(this.level());
               boolean flag = state.getBlock() instanceof DetentionBlock;
               boolean flag1 = false;
               if ((Boolean)CuffedMod.SERVER_CONFIG.ALLOW_BREAKING_OUT_OF_PILLORY.get()) {
                  if (this.pilloryBreakOutProgress >= 100.0F) {
                     if (state.getBlock() instanceof PilloryBlock) {
                        this.level().levelEvent(2001, pos, Block.getId(state));
                        this.level().playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS);
                        this.undetain();
                        this.pilloryBreakOutProgress = 0.0F;
                     }
                  } else {
                     if (state.getBlock() instanceof DetentionBlock detentionBlock) {
                        flag1 = detentionBlock.canDetainPlayer(this.level(), state, (BlockPos)this.entityData.get(DATA_DETAINED_TO_BLOCK), (Player)(Object)this, false);
                     }

                     if (!flag || !flag1) {
                        this.undetain();
                     }
                  }
               }
            }
         }

         if ((Boolean)CuffedMod.SERVER_CONFIG.ALLOW_SELF_RESTRAINING.get()) {
            if (this.swinging) {
               if (!this.hasProcessedSwing) {
                  this.hasProcessedSwing = true;
                  ServerPlayer me = (ServerPlayer)(Object)this;
                  this.attemptToRemoveRestraint(me, cap);
               }
            } else {
               this.hasProcessedSwing = false;
            }
         }
      }
   }

   public boolean attemptToRemoveRestraint(ServerPlayer player, RestrainableCapability cap) {
      boolean armsRestrained = cap.armsRestrained();
      if (!armsRestrained) {
         Vec3 lookAngle = player.getLookAngle();
         if (lookAngle.y >= 0.0058177644F) {
            cap.onInteractedByOther(player, player, 2.0, this.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, true);
            return true;
         } else if (lookAngle.y <= -0.33333334F) {
            cap.onInteractedByOther(player, player, 0.0, this.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, true);
            return true;
         } else {
            cap.onInteractedByOther(player, player, 1.0, this.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND, true);
            return true;
         }
      } else {
         return false;
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"wantsToStopRiding"},
      cancellable = true
   )
   protected void wantsToStopRiding(CallbackInfoReturnable<Boolean> callback) {
      IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability((Player)(Object)this);
      if (cap.restraintsDisabledMovement()) {
         callback.setReturnValue(false);
      }
   }

   @Inject(
      method = {"addAdditionalSaveData"},
      at = {@At("HEAD")}
   )
   public void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo callback) {
      CompoundTag detainedTag = new CompoundTag();
      detainedTag.putInt("Id", this.getDetained());
      detainedTag.putFloat("Rotation", this.getDetainedRotation());
      BlockPos blockPos = (BlockPos)this.entityData.get(DATA_DETAINED_TO_BLOCK);
      detainedTag.putIntArray("Block", new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
      Vector3f pos = (Vector3f)this.entityData.get(DATA_DETAINED_POSITION);
      detainedTag.putFloat("X", pos.x());
      detainedTag.putFloat("Y", pos.y());
      detainedTag.putFloat("Z", pos.z());
      tag.put("Detained", detainedTag);
      if ((Boolean)CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_LOGOUT.get() && this.getNickname() != null) {
         tag.putString("Nickname", this.serializeNickname());
      }

      tag.put("PrivacyRestrictions", this.serializeRestrictions());
   }

   @Inject(
      method = {"readAdditionalSaveData"},
      at = {@At("HEAD")}
   )
   public void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo callback) {
      if (tag.contains("Detained")) {
         CompoundTag t = tag.getCompound("Detained");
         this.setDetained(t.getInt("Id"));
         this.setDetainedRotation(t.getFloat("Rotation"));
         int[] a = t.getIntArray("Block");
         this.setBlockDetainedTo(new BlockPos(a[0], a[1], a[2]));
         this.setDetainedPosition(new Vector3f(t.getFloat("X"), t.getFloat("Y"), t.getFloat("Z")));
      }

      if ((Boolean)CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_LOGOUT.get() && tag.contains("Nickname")) {
         this.setNickname(Serializer.fromJson(tag.getString("Nickname"), this.registryAccess()));
      } else {
         this.setNickname(null);
      }

      this.deserializeRestrictions(tag);
   }

   @Override
   public Component getNickname() {
      return this.entityData.get(DATA_HAS_NICKNAME) ? (Component)this.entityData.get(DATA_NICKNAME) : null;
   }

   @Override
   public void setNickname(@Nullable Component value) {
      if (value == null) {
         this.entityData.set(DATA_HAS_NICKNAME, false);
         this.entityData.set(DATA_NICKNAME, Component.empty());
      } else {
         this.entityData.set(DATA_HAS_NICKNAME, true);
         this.entityData.set(DATA_NICKNAME, value);
      }
   }

   @Override
   public String serializeNickname() {
      return Serializer.toJson(this.getNickname(), this.registryAccess());
   }

   @Override
   public void deserializeNickname(CompoundTag tag) {
      if (tag.contains("Nickname")) {
         this.setNickname(Serializer.fromJson(tag.getString("Nickname"), this.registryAccess()));
      }
   }

   @Override
   public void deserializeNickname(String nickTag) {
      this.setNickname(Serializer.fromJson(nickTag, this.registryAccess()));
   }

   @Override
   public boolean isRestrained() {
      if (!this.level().isClientSide()) {
         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability((Player)(Object)this);
         if (cap != null) {
            return cap.isRestrained();
         }
      }

      return (Integer)this.entityData.get(DATA_RESTRAINT_CODE) > 0;
   }

   @Override
   public int getRestraintCode() {
      return (Integer)this.entityData.get(DATA_RESTRAINT_CODE);
   }

   @Override
   public ResourceLocation getArmRestraintId() {
      return ResourceLocation.bySeparator((String)this.entityData.get(DATA_ARM_RESTRAINT_ID), ':');
   }

   @Override
   public ResourceLocation getLegRestraintId() {
      return ResourceLocation.bySeparator((String)this.entityData.get(DATA_LEG_RESTRAINT_ID), ':');
   }

   @Override
   public ResourceLocation getHeadRestraintId() {
      return ResourceLocation.bySeparator((String)this.entityData.get(DATA_HEAD_RESTRAINT_ID), ':');
   }

   @Override
   public void setRestraintCode(int v) {
      this.entityData.set(DATA_RESTRAINT_CODE, v);
   }

   @Override
   public void setArmRestraintId(ResourceLocation v) {
      this.entityData.set(DATA_ARM_RESTRAINT_ID, v.toString());
   }

   @Override
   public void setLegRestraintId(ResourceLocation v) {
      this.entityData.set(DATA_LEG_RESTRAINT_ID, v.toString());
   }

   @Override
   public void setHeadRestraintId(ResourceLocation v) {
      this.entityData.set(DATA_HEAD_RESTRAINT_ID, v.toString());
   }

   @Override
   public boolean getArmsAreEnchanted() {
      return (Boolean)this.entityData.get(DATA_ARM_ENCHANTED);
   }

   @Override
   public boolean getLegsAreEnchanted() {
      return (Boolean)this.entityData.get(DATA_LEG_ENCHANTED);
   }

   @Override
   public boolean getHeadIsEnchanted() {
      return (Boolean)this.entityData.get(DATA_HEAD_ENCHANTED);
   }

   @Override
   public void setArmsEnchanted(boolean v) {
      this.entityData.set(DATA_ARM_ENCHANTED, v);
   }

   @Override
   public void setLegsEnchanted(boolean v) {
      this.entityData.set(DATA_LEG_ENCHANTED, v);
   }

   @Override
   public void setHeadEnchanted(boolean v) {
      this.entityData.set(DATA_HEAD_ENCHANTED, v);
   }

   @Override
   public void detainToBlock(@Nonnull Level level, Vector3f detainPos, @Nonnull BlockPos pos, int detaintType, float facingRotation) {
      this.setDetained(detaintType);
      this.setDetainedRotation(facingRotation);
      this.setBlockDetainedTo(pos);
      this.setDetainedPosition(detainPos);
   }

   @Override
   public void undetain() {
      this.setDetained(-1);
      this.setDetainedRotation(0.0F);
      this.setBlockDetainedTo(BlockPos.ZERO);
      this.setDetainedPosition(new Vector3f(0.0F, 0.0F, 0.0F));
   }

   @Override
   public int getDetained() {
      return (Integer)this.entityData.get(DATA_DETAINED);
   }

   @Override
   public void setDetained(int value) {
      this.entityData.set(DATA_DETAINED, value);
   }

   @Override
   public float getDetainedRotation() {
      return (Float)this.entityData.get(DATA_DETAINED_ROTATION);
   }

   @Override
   public void setDetainedRotation(float value) {
      this.entityData.set(DATA_DETAINED_ROTATION, value);
   }

   @Override
   public BlockState getBlockDetainedTo(@Nonnull Level level) {
      return level.getBlockState((BlockPos)this.entityData.get(DATA_DETAINED_TO_BLOCK));
   }

   @Override
   public void setBlockDetainedTo(@Nonnull BlockPos pos) {
      this.entityData.set(DATA_DETAINED_TO_BLOCK, pos);
   }

   @Override
   public Vector3f getDetainedPosition() {
      return (Vector3f)this.entityData.get(DATA_DETAINED_POSITION);
   }

   @Override
   public void setDetainedPosition(Vector3f value) {
      this.entityData.set(DATA_DETAINED_POSITION, value);
   }

   @Override
   public IPrivacyOperand.PrivacyRestriction getAnchoringRestrictions() {
      return this.getRestriction("cuffed:anchoring");
   }

   @Override
   public void setAnchoringRestrictions(IPrivacyOperand.PrivacyRestriction newRestriction) {
      this.setRestriction("cuffed:anchoring", newRestriction);
   }

   @Override
   public IPrivacyOperand.PrivacyRestriction getDetainingRestrictions() {
      return this.getRestriction("cuffed:detaining");
   }

   @Override
   public void setDetainingRestrictions(IPrivacyOperand.PrivacyRestriction newRestriction) {
      this.setRestriction("cuffed:detaining", newRestriction);
   }

   @Override
   public IPrivacyOperand.PrivacyRestriction getNicknamingRestrictions() {
      return this.getRestriction("cuffed:nicknaming");
   }

   @Override
   public void setNicknamingRestrictions(IPrivacyOperand.PrivacyRestriction newRestriction) {
      this.setRestriction("cuffed:nicknaming", newRestriction);
   }

   @Override
   public IPrivacyOperand.PrivacyRestriction getRestrainingRestrictions() {
      return this.getRestriction("cuffed:restraining");
   }

   @Override
   public void setRestrainingRestrictions(IPrivacyOperand.PrivacyRestriction newRestriction) {
      this.setRestriction("cuffed:restraining", newRestriction);
   }

   @Override
   public void setRestriction(String key, IPrivacyOperand.PrivacyRestriction newRestriction) {
      this.privacyRestrictions.put(key, newRestriction);
   }

   @Override
   public IPrivacyOperand.PrivacyRestriction getRestriction(String key) {
      return this.privacyRestrictions.get(key);
   }

   @Override
   public void deserializeRestrictions(CompoundTag tag) {
      if (tag.contains("PrivacyRestrictions")) {
         this.privacyRestrictions.clear();
         if (tag.size() == 0) {
            ListTag list = tag.getList("PrivacyRestrictions", 10);

            for (int i = 0; i < list.size(); i++) {
               CompoundTag t = list.getCompound(i);
               this.privacyRestrictions.put(t.getString("Id"), IPrivacyOperand.PrivacyRestriction.fromInteger(t.getInt("RestrictionLevel")));
            }

            return;
         }
      }

      this.writeDefaultRestrictions();
   }

   @Override
   public ListTag serializeRestrictions() {
      ListTag list = new ListTag();
      if (this.privacyRestrictions.size() == 0) {
         this.writeDefaultRestrictions();
      }

      for (Entry<String, IPrivacyOperand.PrivacyRestriction> r : this.privacyRestrictions.entrySet()) {
         CompoundTag tag = new CompoundTag();
         tag.putInt("RestrictionLevel", r.getValue().toInteger());
         tag.putString("Id", r.getKey());
         list.add(tag);
      }

      return list;
   }

   @Override
   public void writeDefaultRestrictions() {
      this.privacyRestrictions.clear();
      this.privacyRestrictions.put("cuffed:anchoring", IPrivacyOperand.PrivacyRestriction.NEVER);
      this.privacyRestrictions.put("cuffed:detaining", IPrivacyOperand.PrivacyRestriction.NEVER);
      this.privacyRestrictions.put("cuffed:nicknaming", IPrivacyOperand.PrivacyRestriction.NEVER);
      this.privacyRestrictions.put("cuffed:restraining", IPrivacyOperand.PrivacyRestriction.NEVER);
   }

   @Shadow
   public Iterable<ItemStack> getArmorSlots() {
      return null;
   }

   @Shadow
   public ItemStack getItemBySlot(@Nonnull EquipmentSlot p_21127_) {
      return null;
   }

   @Shadow
   public void setItemSlot(@Nonnull EquipmentSlot p_21036_, @Nonnull ItemStack p_21037_) {
   }

   @Shadow
   public HumanoidArm getMainArm() {
      return null;
   }
}
