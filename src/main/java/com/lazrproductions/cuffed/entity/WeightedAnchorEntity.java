package com.lazrproductions.cuffed.entity;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModEntityTypes;
import com.lazrproductions.cuffed.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LivingEntity.Fallsounds;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

public class WeightedAnchorEntity extends LivingEntity {
   private static final EntityDataAccessor<Boolean> DATA_ENCHANTED = SynchedEntityData.defineId(WeightedAnchorEntity.class, EntityDataSerializers.BOOLEAN);
   ItemEnchantments enchantments = ItemEnchantments.EMPTY;

   public WeightedAnchorEntity(EntityType<? extends LivingEntity> type, Level level) {
      super(type, level);
   }

   public WeightedAnchorEntity(Level world, BlockPos pos) {
      super((EntityType)ModEntityTypes.WEIGHTED_ANCHOR.get(), world);
      this.setPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
   }

   public static WeightedAnchorEntity createFromItem(@Nonnull Level level, @Nonnull ItemStack stack, @Nonnull BlockPos pos) {
      WeightedAnchorEntity entity = new WeightedAnchorEntity(level, pos);
      entity.setEnchantments((ItemEnchantments)stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
      return entity;
   }

   public ItemStack getDroppedItem() {
      ItemStack stack = new ItemStack((ItemLike)ModItems.WEIGHTED_ANCHOR_ITEM.get(), 1);
      stack.set(DataComponents.ENCHANTMENTS, this.getEnchantments());
      return stack;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ENCHANTED, false);
   }

   public void tick() {
      if (!this.level().isClientSide()) {
         this.entityData.set(DATA_ENCHANTED, this.getEnchantments().size() > 0);
         if (this.isInWaterOrBubble() && this.getEnchantmentLevel(ModEnchantments.BUOYANT) >= 1) {
            this.addDeltaMovement(new Vec3(0.0, 0.023F, 0.0));
         }
      }

      super.tick();
   }

   public boolean isInvulnerable() {
      return true;
   }

   public boolean isInvulnerableTo(@Nonnull DamageSource source) {
      return !source.is(DamageTypes.GENERIC_KILL);
   }

   public boolean canDrownInFluidType(FluidType type) {
      return false;
   }

   public Fallsounds getFallSounds() {
      return new Fallsounds(SoundEvents.CHAIN_PLACE, SoundEvents.ANVIL_FALL);
   }

   public ItemStack getPickedResult(HitResult target) {
      return this.getDroppedItem();
   }

   protected Vec3 getLeashOffset() {
      return new Vec3(0.0, 0.5, 0.0);
   }

   public Vec3 getRopeHoldPosition(float p_20347_) {
      return this.position().add(this.getLeashOffset());
   }

   public boolean isAttackable() {
      return false;
   }

   public InteractionResult interact(@Nonnull Player interactor, @Nonnull InteractionHand hand) {
      if (this.level().isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         if (hand == InteractionHand.MAIN_HAND) {
            if (!interactor.isCrouching() && (Boolean)CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS.get()) {
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
                  for (LivingEntity entityx : list) {
                     IAnchorableEntity anchorableEntity = (IAnchorableEntity)entityx;
                     if (anchorableEntity.isAnchored() && anchorableEntity.getAnchor() == this) {
                        anchorableEntity.setAnchoredTo(null);
                        flag1 = true;
                     }
                  }

                  if (flag1) {
                     this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
                  }
               }

               if (flag) {
                  this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7F, 1.0F);
               }

               if (flag1) {
                  this.level().playSound(null, this.blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
               }

               if (flag || flag1) {
                  this.gameEvent(GameEvent.BLOCK_ATTACH, interactor);
               }

               return InteractionResult.CONSUME;
            }

            ItemStack stack = this.getDroppedItem();
            ItemEntity entityxx = new ItemEntity(this.level(), this.position().x(), this.position().y(), this.position().z(), stack);
            this.level().addFreshEntity(entityxx);
            this.level().playSound(null, this.blockPosition(), SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
            this.discard();
         }

         return InteractionResult.FAIL;
      }
   }

   public void addAdditionalSaveData(@Nonnull CompoundTag tag) {
      tag.put(
         "Enchantments",
         (Tag)ItemEnchantments.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.enchantments).getOrThrow()
      );
      super.addAdditionalSaveData(tag);
   }

   public void readAdditionalSaveData(@Nonnull CompoundTag tag) {
      if (tag.contains("Enchantments")) {
         this.enchantments = (ItemEnchantments)ItemEnchantments.CODEC
            .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag.get("Enchantments"))
            .getOrThrow();
      } else {
         this.enchantments = ItemEnchantments.EMPTY;
      }

      super.readAdditionalSaveData(tag);
   }

   public ItemEnchantments getEnchantments() {
      return this.enchantments;
   }

   public void setEnchantments(ItemEnchantments tag) {
      this.enchantments = tag;
   }

   public boolean hasEnchantment(Holder<Enchantment> enchantment) {
      return this.getEnchantmentLevel(enchantment) > 0;
   }

   public boolean hasEnchantment(ResourceKey<Enchantment> enchantmentKey) {
      return this.getEnchantmentLevel(enchantmentKey) > 0;
   }

   public int getEnchantmentLevel(Holder<Enchantment> enchantment) {
      return this.enchantments.getLevel(enchantment);
   }

   public int getEnchantmentLevel(ResourceKey<Enchantment> enchantmentKey) {
      Optional<Reference<Enchantment>> opt = this.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantmentKey);
      return opt.isPresent() ? this.getEnchantmentLevel((Holder<Enchantment>)opt.get()) : 0;
   }

   public void enchant(Holder<Enchantment> enchantment, int value) {
      Mutable mutable = new Mutable(this.enchantments);
      mutable.set(enchantment, value);
      this.enchantments = mutable.toImmutable();
   }

   public boolean getIsEnchanted() {
      return (Boolean)this.entityData.get(DATA_ENCHANTED);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.MOVEMENT_SPEED, 0.0);
   }

   public Iterable<ItemStack> getArmorSlots() {
      return new ArrayList<>(0);
   }

   public ItemStack getItemBySlot(@Nonnull EquipmentSlot p_21127_) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(@Nonnull EquipmentSlot p_21036_, @Nonnull ItemStack p_21037_) {
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }
}
