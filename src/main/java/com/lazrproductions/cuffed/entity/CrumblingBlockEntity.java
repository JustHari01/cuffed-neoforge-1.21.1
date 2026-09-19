package com.lazrproductions.cuffed.entity;

import com.lazrproductions.cuffed.init.ModEntityTypes;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CrumblingBlockEntity extends Entity {
   static int CRUMBLE_STAGE_TIME = 20;
   private BlockState blockState = Blocks.SAND.defaultBlockState();
   public int time = CRUMBLE_STAGE_TIME * 5;
   protected static final EntityDataAccessor<Integer> DATA_CRUMBLE_PROGRESS = SynchedEntityData.defineId(CrumblingBlockEntity.class, EntityDataSerializers.INT);

   public CrumblingBlockEntity(EntityType<? extends CrumblingBlockEntity> type, Level level) {
      super(type, level);
   }

   public CrumblingBlockEntity(Level world, BlockPos pos, BlockState state, int crumbleProgress) {
      super((EntityType)ModEntityTypes.CRUMBLING_BLOCK.get(), world);
      this.setPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
      this.setCrumbleProgress(crumbleProgress, 1);
      this.blockState = state;
      this.time = CRUMBLE_STAGE_TIME * 5;
   }

   public static CrumblingBlockEntity crumbleBlock(Level level, BlockPos pos, BlockState state, int crumbleStrength) {
      if (state.isSolid()) {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         for (CrumblingBlockEntity e : level.getEntitiesOfClass(
            CrumblingBlockEntity.class, new AABB((double)x - 1.0, (double)y - 1.0, (double)z - 1.0, (double)x + 1.0, (double)y + 1.0, (double)z + 1.0)
         )) {
            if (e.blockPosition().equals(pos) && e.blockState == state) {
               e.setCrumbleProgress(e.getCrumbleProgress() + crumbleStrength, 5);
               return e;
            }
         }

         CrumblingBlockEntity ex = new CrumblingBlockEntity(level, pos, state, crumbleStrength);
         level.addFreshEntity(ex);
         return ex;
      } else {
         return null;
      }
   }

   public void setCrumbleProgress(int b, int timeMul) {
      this.entityData.set(DATA_CRUMBLE_PROGRESS, b);
      this.time = CRUMBLE_STAGE_TIME * timeMul;
   }

   public int getCrumbleProgress() {
      return (Integer)this.entityData.get(DATA_CRUMBLE_PROGRESS);
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_CRUMBLE_PROGRESS, 1);
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public void tick() {
      Level l = this.level();
      if (l.getBlockState(this.blockPosition()).isAir()) {
         this.discard();
      } else if (this.getCrumbleProgress() < 5) {
         if (this.getCrumbleProgress() <= 0) {
            this.discard();
         }
      } else {
         l.destroyBlock(this.blockPosition(), true);
         this.discard();
      }
   }

   protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
      tag.putInt("Time", this.time);
      tag.putInt("CrumbleProgress", this.getCrumbleProgress());
   }

   protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
      this.time = tag.getInt("Time");
      this.setCrumbleProgress(tag.getInt("CrumbleProgress"), 1);
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean displayFireAnimation() {
      return false;
   }
}
