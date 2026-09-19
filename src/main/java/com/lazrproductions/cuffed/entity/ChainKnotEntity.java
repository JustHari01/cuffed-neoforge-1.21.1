package com.lazrproductions.cuffed.entity;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChainKnotEntity extends HangingEntity {
   public ChainKnotEntity(EntityType<? extends HangingEntity> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public ChainKnotEntity(Level world, BlockPos pos) {
      super((EntityType)ModEntityTypes.CHAIN_KNOT.get(), world, pos);
      this.setPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
   }

   public boolean isOnFence() {
      return !this.level().getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public void dropItem(@Nullable Entity p_31837_) {
      this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
   }

   public boolean hurt(@Nonnull DamageSource source, float f) {
      if (source.getEntity() instanceof IAnchorableEntity a && a.getAnchor() == this) {
         return false;
      }

      return super.hurt(source, f);
   }

   public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {
      if (this.level().isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (((IAnchorableEntity)interactor).isAnchored()) {
         return InteractionResult.PASS;
      } else {
         boolean flag = false;
         double maxDist = (double)((Float)CuffedMod.SERVER_CONFIG.ANCHORING_SUFFOCATION_LENGTH.get() + 5.0F);
         List<LivingEntity> list = this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(
                  this.getX() - maxDist - 2.0,
                  this.getY() - maxDist - 2.0,
                  this.getZ() - maxDist - 2.0,
                  this.getX() + maxDist + 2.0,
                  this.getY() + maxDist + 2.0,
                  this.getZ() + maxDist + 2.0
               )
            );

         for (LivingEntity entity : list) {
            IAnchorableEntity anchorableEntity = (IAnchorableEntity)entity;
            if (anchorableEntity.getAnchor() == interactor) {
               anchorableEntity.setAnchoredTo(this);
               flag = true;
            }
         }

         boolean flag1 = false;
         if (!flag) {
            this.level().playSound(null, this.pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);

            for (LivingEntity entityx : list) {
               IAnchorableEntity anchorableEntity = (IAnchorableEntity)entityx;
               if (anchorableEntity.isAnchored() && anchorableEntity.getAnchor() == this) {
                  anchorableEntity.setAnchoredTo(null);
                  flag1 = true;
               }
            }

            this.discard();
         }

         if (flag) {
            this.level().playSound(null, this.pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7F, 1.0F);
         }

         if (flag1) {
            this.level().playSound(null, this.pos, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
         }

         if (flag || flag1) {
            this.gameEvent(GameEvent.BLOCK_ATTACH, interactor);
         }

         return InteractionResult.CONSUME;
      }
   }

   public static ChainKnotEntity getOrCreateKnot(Level level, BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7F, 1.0F);

      for (ChainKnotEntity leashfenceknotentity : level.getEntitiesOfClass(
         ChainKnotEntity.class, new AABB((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
      )) {
         if (leashfenceknotentity.getPos().equals(pos)) {
            return leashfenceknotentity;
         }
      }

      ChainKnotEntity newEntity = new ChainKnotEntity(level, pos);
      level.addFreshEntity(newEntity);
      return newEntity;
   }

   public static ChainKnotEntity bindEntityToNewOrExistingKnot(LivingEntity entity, Level level, BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7F, 1.0F);

      for (ChainKnotEntity knot : level.getEntitiesOfClass(
         ChainKnotEntity.class, new AABB((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
      )) {
         if (knot.getPos().equals(pos)) {
            ((IAnchorableEntity)entity).setAnchor(knot);
            return knot;
         }
      }

      ChainKnotEntity newKnot = new ChainKnotEntity(level, pos);
      ((IAnchorableEntity)entity).setAnchor(newKnot);
      level.addFreshEntity(newKnot);
      return newKnot;
   }

   public void setPos(double x, double y, double z) {
      super.setPos((double)Mth.floor(x) + 0.5, (double)Mth.floor(y) + 0.5, (double)Mth.floor(z) + 0.5);
   }

   protected void setDirection(@Nonnull Direction pFacingDirection) {
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 1024.0;
   }

   public boolean survives() {
      return this.level().getBlockState(this.pos).is(BlockTags.FENCES) || this.level().getBlockState(this.pos).is(Blocks.TRIPWIRE_HOOK);
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.CHAIN_PLACE, 1.0F, 1.0F);
   }

   public Vec3 getRopeHoldPosition(float partialTick) {
      return this.getPosition(partialTick).add(this.getLeashOffset(partialTick));
   }

   public Vec3 getLeashOffset(float partialTick) {
      return new Vec3(0.0, 0.0625 + (!this.isOnFence() ? 0.2 : 0.0), 0.0);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.CHAIN);
   }

   protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
      double d0 = 0.28125;
      double d1 = 0.5625;
      return new AABB(
         (double)pos.getX() + 0.5 - d0,
         (double)pos.getY() + 0.375,
         (double)pos.getZ() + 0.5 - d0,
         (double)pos.getX() + 0.5 + d0,
         (double)pos.getY() + 0.375 + d1,
         (double)pos.getZ() + 0.5 + d0
      );
   }
}
