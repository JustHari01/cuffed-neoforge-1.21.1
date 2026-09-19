package com.lazrproductions.cuffed.entity;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PadlockEntity extends HangingEntity {
   private static final EntityDataAccessor<Boolean> DATA_LOCKED = SynchedEntityData.defineId(PadlockEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_REINFORCED = SynchedEntityData.defineId(PadlockEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<String> DATA_ITEM_USED_TO_REINFORCE = SynchedEntityData.defineId(PadlockEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Optional<UUID>> DATA_LOCK_ID = SynchedEntityData.defineId(PadlockEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Boolean> DATA_HAS_BEEN_BOUND = SynchedEntityData.defineId(PadlockEntity.class, EntityDataSerializers.BOOLEAN);

   public PadlockEntity(EntityType<? extends HangingEntity> type, Level level) {
      super(type, level);
   }

   public PadlockEntity(Level world, BlockPos pos) {
      super((EntityType)ModEntityTypes.PADLOCK.get(), world, pos);
      this.setPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
      this.entityData.set(DATA_LOCK_ID, Optional.of(UUID.randomUUID()));
   }

   public boolean isOnSuitableBlock() {
      return this.level().getBlockState(this.pos).is(ModTags.Blocks.LOCKABLE_BLOCKS);
   }

   public void dropItem(@Nullable Entity entity) {
      this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
      float xO = this.getYRot() == 90.0F ? -1.0F : (this.getYRot() != 270.0F && this.getYRot() != -90.0F ? 0.0F : 1.0F);
      float zO = this.getYRot() == 0.0F ? 1.0F : (this.getYRot() != 180.0F && this.getYRot() != -180.0F ? 0.0F : -1.0F);
      ItemEntity itementity = new ItemEntity(
         this.level(),
         (double)((float)this.pos.getX() + 0.5F + xO),
         (double)((float)this.pos.getY() + 0.5F),
         (double)((float)this.pos.getZ() + 0.5F + zO),
         new ItemStack((ItemLike)ModItems.PADLOCK.get())
      );
      itementity.setDefaultPickUpDelay();
      this.level().addFreshEntity(itementity);
      if (this.isReinforced()) {
         String i = this.getItemUsedToReinforce();
         ItemStack stack = new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", i)));
         ItemEntity e = new ItemEntity(
            this.level(),
            (double)((float)this.pos.getX() + 0.5F + xO),
            (double)((float)this.pos.getY() + 0.5F),
            (double)((float)this.pos.getZ() + 0.5F + zO),
            stack
         );
         e.setDefaultPickUpDelay();
         this.level().addFreshEntity(e);
      }
   }

   public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {
      if (this.level().isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack stack = interactor.getItemInHand(hand);
         if (stack.is((Item)ModItems.CREATIVE_RESTRAINT_CUTTER.get())) {
            interactor.awardStat(Stats.ITEM_USED.get((Item)ModItems.CREATIVE_KEY.get()));
            if (!interactor.isCrouching()) {
               this.setLocked(!this.isLocked());
               if (this.isLocked()) {
                  interactor.displayClientMessage(Component.literal("Padlock locked."), true);
               } else {
                  interactor.displayClientMessage(Component.literal("Padlock unlocked."), true);
               }

               this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
               return InteractionResult.SUCCESS;
            } else {
               KeyItem.removeBoundLock(stack);
               this.RemoveLock();
               return InteractionResult.SUCCESS;
            }
         } else {
            if (stack.is((Item)ModItems.KEY.get())) {
               if (KeyItem.isBoundToLock(stack, this.getLockId())) {
                  interactor.awardStat(Stats.ITEM_USED.get((Item)ModItems.KEY.get()));
                  if (!interactor.isCrouching()) {
                     this.setLocked(!this.isLocked());
                     if (this.isLocked()) {
                        interactor.displayClientMessage(Component.literal("Padlock locked."), true);
                     } else {
                        interactor.displayClientMessage(Component.literal("Padlock unlocked."), true);
                     }

                     this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
                     return InteractionResult.SUCCESS;
                  }

                  KeyItem.removeBoundLock(stack);
                  this.RemoveLock();
                  return InteractionResult.SUCCESS;
               }

               if (!this.getHasBeenBound() && KeyItem.tryToSetBoundId(interactor, stack, this.getLockId(), "Padlock")) {
                  this.bind();
                  return InteractionResult.SUCCESS;
               }
            }

            if (stack.is((Item)ModItems.KEY_RING.get())) {
               if (KeyRingItem.hasBoundId(stack, this.getLockId())) {
                  interactor.awardStat(Stats.ITEM_USED.get((Item)ModItems.KEY_RING.get()));
                  if (!interactor.isCrouching()) {
                     this.setLocked(!this.isLocked());
                     if (this.isLocked()) {
                        interactor.displayClientMessage(Component.literal("Padlock locked."), true);
                     } else {
                        interactor.displayClientMessage(Component.literal("Padlock unlocked."), true);
                     }

                     this.playSound(SoundEvents.IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
                     return InteractionResult.CONSUME;
                  }

                  KeyRingItem.removeBoundId(stack, this.getLockId());
                  this.RemoveLock();
                  return InteractionResult.CONSUME;
               }

               if (KeyRingItem.canBindLock(stack)
                  && !this.getHasBeenBound()
                  && KeyRingItem.tryToAddBoundId(interactor, stack, this.getLockId(), "entity.cuffed.padlock")) {
                  this.bind();
                  return InteractionResult.CONSUME;
               }
            }

            if (stack.is(ModTags.Items.CAN_REINFORCE_PADLOCK) && !this.isReinforced()) {
               interactor.awardStat(Stats.ITEM_USED.get(stack.getItem()));
               this.setReinforced(true);
               this.setItemUsedToReinforce(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
               stack.shrink(1);
               this.playSound(SoundEvents.NETHERITE_BLOCK_PLACE, 1.0F, 1.0F);
               return InteractionResult.CONSUME;
            } else if (stack.is((Item)ModItems.LOCKPICK.get())) {
               CuffedAPI.Networking.sendLockpickBeginPickingLockPacketToClient(
                  (ServerPlayer)interactor,
                  this.getId(),
                  this.isReinforced()
                     ? (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS.get()
                     : (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_PADLOCKS.get(),
                  this.isReinforced()
                     ? (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS.get()
                     : (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_PADLOCKS.get()
               );
               return InteractionResult.SUCCESS;
            } else if (stack.is((Item)ModItems.CREATIVE_BIND_BREAKER.get()) && this.getHasBeenBound()) {
               this.resetBindings();
               interactor.sendSystemMessage(Component.translatable("item.cuffed.creative_bind_breaker.use", new Object[]{this.getDisplayName().getString()}));
               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.FAIL;
            }
         }
      }
   }

   public static PadlockEntity getOrCreateLockAt(Level level, BlockPos pos, Direction direction) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();

      for (PadlockEntity padlockentity : level.getEntitiesOfClass(
         PadlockEntity.class, new AABB((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
      )) {
         if (padlockentity.getPos().equals(pos)) {
            return padlockentity;
         }
      }

      PadlockEntity padlockentity1 = new PadlockEntity(level, pos);
      padlockentity1.setRot(direction.toYRot(), 0.0F);
      level.addFreshEntity(padlockentity1);
      return padlockentity1;
   }

   public static PadlockEntity getLockAt(Level level, BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();

         for (PadlockEntity padlockentity : level.getEntitiesOfClass(
            PadlockEntity.class, new AABB((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
         )) {
            if (padlockentity.getPos().equals(pos)) {
               return padlockentity;
            }
         }

         return null;
      }
   }

   public void RemoveLock() {
      float xO = this.getYRot() == 90.0F ? -1.0F : (this.getYRot() != 270.0F && this.getYRot() != -90.0F ? 0.0F : 1.0F);
      float zO = this.getYRot() == 0.0F ? 1.0F : (this.getYRot() != 180.0F && this.getYRot() != -180.0F ? 0.0F : -1.0F);
      ItemEntity itementity = new ItemEntity(
         this.level(),
         (double)((float)this.pos.getX() + 0.5F + xO),
         (double)((float)this.pos.getY() + 0.5F),
         (double)((float)this.pos.getZ() + 0.5F + zO),
         new ItemStack((ItemLike)ModItems.PADLOCK.get())
      );
      itementity.setDefaultPickUpDelay();
      this.level().addFreshEntity(itementity);
      if (this.isReinforced()) {
         String i = this.getItemUsedToReinforce();
         ItemStack stack = new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(ResourceLocation.bySeparator(i, ':')));
         ItemEntity e = new ItemEntity(
            this.level(),
            (double)((float)this.pos.getX() + 0.5F + xO),
            (double)((float)this.pos.getY() + 0.5F),
            (double)((float)this.pos.getZ() + 0.5F + zO),
            stack
         );
         e.setDefaultPickUpDelay();
         this.level().addFreshEntity(e);
      }

      this.playSound(SoundEvents.CHAIN_BREAK, 1.0F, 1.0F);
      this.discard();
   }

   public boolean hurt(@Nonnull DamageSource source, float amount) {
      return false;
   }

   public void setPos(double x, double y, double z) {
      super.setPos((double)Mth.floor(x) + 0.5, (double)Mth.floor(y) + 0.5, (double)Mth.floor(z) + 0.5);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 1024.0;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_LOCKED, false);
      builder.define(DATA_REINFORCED, false);
      builder.define(DATA_ITEM_USED_TO_REINFORCE, "empty");
      builder.define(DATA_LOCK_ID, Optional.empty());
      builder.define(DATA_HAS_BEEN_BOUND, false);
   }

   public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Locked", this.isLocked());
      tag.putBoolean("Reinforced", this.isReinforced());
      tag.putUUID("LockId", this.getLockId());
      tag.putBoolean("HasBeenBound", this.getHasBeenBound());
      tag.putString("ItemUsedToReinforce", this.getItemUsedToReinforce());
   }

   public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.entityData.set(DATA_LOCKED, tag.getBoolean("Locked"));
      this.setLocked(tag.getBoolean("Locked"));
      this.entityData.set(DATA_REINFORCED, tag.getBoolean("Reinforced"));
      this.setReinforced(tag.getBoolean("Reinforced"));
      this.setItemUsedToReinforce(tag.getString("ItemUsedToReinforce"));
      this.entityData.set(DATA_LOCK_ID, Optional.of(tag.getUUID("LockId")));
      this.entityData.set(DATA_HAS_BEEN_BOUND, tag.getBoolean("HasBeenBound"));
   }

   public boolean survives() {
      return !this.level().getBlockState(this.pos).isAir();
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.NETHERITE_BLOCK_PLACE, 1.0F, 1.0F);
   }

   public ItemStack getPickResult() {
      return new ItemStack((ItemLike)ModItems.PADLOCK.get());
   }

   public void setLocked(boolean value) {
      this.entityData.set(DATA_LOCKED, value);
   }

   public void setReinforced(boolean value) {
      this.entityData.set(DATA_REINFORCED, value);
   }

   public boolean isLocked() {
      return (Boolean)this.entityData.get(DATA_LOCKED);
   }

   public boolean isReinforced() {
      return (Boolean)this.entityData.get(DATA_REINFORCED);
   }

   public String getItemUsedToReinforce() {
      return (String)this.entityData.get(DATA_ITEM_USED_TO_REINFORCE);
   }

   public void setItemUsedToReinforce(String value) {
      this.entityData.set(DATA_ITEM_USED_TO_REINFORCE, value);
   }

   public UUID getLockId() {
      return (UUID)((Optional)this.entityData.get(DATA_LOCK_ID)).get();
   }

   public boolean getHasBeenBound() {
      return (Boolean)this.entityData.get(DATA_HAS_BEEN_BOUND);
   }

   public void bind() {
      this.entityData.set(DATA_HAS_BEEN_BOUND, true);
   }

   public void resetBindings() {
      this.setLocked(false);
      this.entityData.set(DATA_HAS_BEEN_BOUND, false);
      this.entityData.set(DATA_LOCK_ID, Optional.of(UUID.randomUUID()));
   }

   protected AABB calculateBoundingBox(BlockPos pos, Direction direction) {
      return new AABB(
         (double)pos.getX() + 0.5 - 0.52,
         (double)pos.getY() + 0.3 - 0.12,
         (double)pos.getZ() + 0.5 - 0.52,
         (double)pos.getX() + 0.5 + 0.52,
         (double)pos.getY() + 0.3 + 0.13,
         (double)pos.getZ() + 0.5 + 0.52
      );
   }
}
