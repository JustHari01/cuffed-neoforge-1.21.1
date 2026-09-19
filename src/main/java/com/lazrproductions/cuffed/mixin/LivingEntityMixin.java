package com.lazrproductions.cuffed.mixin;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements IAnchorableEntity {
   private static final EntityDataAccessor<Integer> DATA_ANCHOR_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
   private static final String ANCHOR_TAG = "Anchor";
   @Nullable
   private Entity anchor = null;
   private boolean shouldBeLoaded = false;
   private UUID uuidToBeLoaded;
   boolean wasAnchored;

   public LivingEntityMixin(EntityType<?> type, Level level) {
      super(type, level);
   }

   @Override
   public boolean isAnchored() {
      return this.getAnchor() != null;
   }

   @Override
   public Entity getAnchor() {
      return this.anchor;
   }

   @Override
   public Entity getAnchorClientSide() {
      return this.level().getEntity((Integer)this.entityData.get(DATA_ANCHOR_ID));
   }

   @Override
   public void setAnchoredTo(@Nullable Entity e) {
      if (!this.level().isClientSide()) {
         if (e == null) {
            this.setAnchor(null);
            this.wasAnchored = false;
            ItemStack stack = new ItemStack(Items.CHAIN, 1);
            ItemEntity en = new ItemEntity(this.level(), this.position().x(), this.position().y(), this.position().z(), stack);
            en.setDefaultPickUpDelay();
            this.level().addFreshEntity(en);
         } else {
            this.setAnchor(e);
         }
      }
   }

   @Override
   public void setAnchor(@Nullable Entity e) {
      this.anchor = e;
   }

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("TAIL")}
   )
   private void onDefineSynchedData(Builder builder, CallbackInfo info) {
      builder.define(DATA_ANCHOR_ID, -1);
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void onTick(CallbackInfo callback) {
      LivingEntity thisEntity = (LivingEntity)(Object)this;
      if (!this.level().isClientSide()) {
         if (this.shouldBeLoaded && this.level() instanceof ServerLevel l) {
            Entity a = l.getEntity(this.uuidToBeLoaded);
            this.setAnchor(a);
            if (a != null) {
               this.shouldBeLoaded = false;
            }
         }

         if ((this.anchor == null || this.anchor.isRemoved()) && this.wasAnchored) {
            this.setAnchoredTo(null);
            this.wasAnchored = false;
         }

         if (this.anchor != null) {
            this.wasAnchored = true;
            this.entityData.set(DATA_ANCHOR_ID, this.anchor.getId());
            double minDist = (double)((Float)CuffedMod.SERVER_CONFIG.ANCHORING_MAX_CHAIN_LENGTH.get()).floatValue();
            double maxDist = (double)(this.fallDistance > 0.2F
                  ? (Float)CuffedMod.SERVER_CONFIG.ANCHORING_MAX_CHAIN_LENGTH.get()
                  : (Float)CuffedMod.SERVER_CONFIG.ANCHORING_SUFFOCATION_LENGTH.get())
               .floatValue();
            if ((double)this.distanceTo(this.getAnchor()) > minDist) {
               float distance = this.distanceTo(this.getAnchor());
               double dx = (this.getAnchor().getX() - this.getX()) / (double)distance;
               double dy = (this.getAnchor().getY() - this.getY()) / (double)distance;
               double dz = (this.getAnchor().getZ() - this.getZ()) / (double)distance;
               this.setDeltaMovement(
                  Math.copySign(dx * dx * ((double)distance / 5.0) * 0.45, dx),
                  Math.copySign(dy * dy * ((double)distance / 5.0) * 0.45, dy),
                  Math.copySign(dz * dz * ((double)distance / 5.0) * 0.45, dz)
               );
               if ((double)this.distanceTo(this.getAnchor()) > maxDist) {
                  this.hurt(ModDamageTypes.GetModSource(this, ModDamageTypes.HANG, this.anchor), 2.0F);
               }
            }
         } else {
            this.entityData.set(DATA_ANCHOR_ID, -1);
         }
      } else if (thisEntity instanceof Player player && this.getAnchorClientSide() != null) {
         double minDist = (double)((Float)CuffedMod.SERVER_CONFIG.ANCHORING_MAX_CHAIN_LENGTH.get()).floatValue();
         if ((double)this.distanceTo(this.getAnchorClientSide()) > minDist) {
            float distance = this.distanceTo(this.getAnchorClientSide());
            double dx = (this.getAnchorClientSide().getX() - this.getX()) / (double)distance;
            double dy = (this.getAnchorClientSide().getY() - this.getY()) / (double)distance;
            double dz = (this.getAnchorClientSide().getZ() - this.getZ()) / (double)distance;
            player.setDeltaMovement(
               Math.copySign(dx * dx * ((double)distance / 5.0) * 0.45, dx),
               Math.copySign(dy * dy * ((double)distance / 5.0) * 0.45, dy),
               Math.copySign(dz * dz * ((double)distance / 5.0) * 0.45, dz)
            );
         }
      }
   }

   @Inject(
      method = {"addAdditionalSaveData"},
      at = {@At("TAIL")}
   )
   private void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
      if (!this.level().isClientSide() && this.isAnchored()) {
         tag.putUUID("Anchor", this.getAnchor().getUUID());
      }
   }

   @Inject(
      method = {"readAdditionalSaveData"},
      at = {@At("TAIL")}
   )
   private void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
      if (!this.level().isClientSide()) {
         if (tag.contains("Anchor")) {
            this.uuidToBeLoaded = tag.getUUID("Anchor");
            this.shouldBeLoaded = true;
         } else {
            this.setAnchor(null);
         }
      }
   }
}
