package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.items.TrayItem;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class TrayBlockEntity extends BlockEntity {
   private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

   public TrayBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.TRAY.get(), pos, state);
   }

   public InteractionResult use(
      @Nonnull BlockState state,
      @Nonnull Level level,
      @Nonnull BlockPos pos,
      @Nonnull Player interacting,
      @Nonnull InteractionHand hand,
      @Nonnull BlockHitResult hitResult
   ) {
      if (hand == InteractionHand.MAIN_HAND) {
         ItemStack itemInMainHand = interacting.getItemInHand(hand);
         if (itemInMainHand.isEmpty()) {
            if (!interacting.isCrouching()) {
               if (this.hasFood()) {
                  float nutritionRatio = 1.0F;
                  if (this.hasFork()) {
                     nutritionRatio += 0.33333F;
                  }

                  if (this.hasSpoon()) {
                     nutritionRatio += 0.33333F;
                  }

                  if (this.hasKnife()) {
                     nutritionRatio += 0.33333F;
                  }

                  ItemStack food = this.getFood();
                  food.getFoodProperties(interacting);
                  this.eat(level, interacting, food, nutritionRatio);
                  this.sendUpdate(level, pos, state);
                  return InteractionResult.SUCCESS;
               }
            } else {
               ItemStack food = this.getFood().copy();
               ItemStack fork = this.getFork().copy();
               ItemStack spoon = this.getSpoon().copy();
               ItemStack knife = this.getKnife().copy();
               if (this.removeItem(0)) {
                  interacting.setItemInHand(hand, food);
                  level.playSound(
                     (Player)null,
                     (double)((float)this.getBlockPos().getX() + 0.5F),
                     (double)this.getBlockPos().getY(),
                     (double)((float)this.getBlockPos().getZ() + 0.5F),
                     SoundEvents.COD_FLOP,
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
                  );
                  this.sendUpdate(level, pos, state);
                  return InteractionResult.SUCCESS;
               }

               if (this.removeItem(1)) {
                  interacting.setItemInHand(hand, fork);
                  level.playSound(
                     (Player)null,
                     (double)((float)this.getBlockPos().getX() + 0.5F),
                     (double)this.getBlockPos().getY(),
                     (double)((float)this.getBlockPos().getZ() + 0.5F),
                     SoundEvents.LANTERN_HIT,
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
                  );
                  this.sendUpdate(level, pos, state);
                  return InteractionResult.SUCCESS;
               }

               if (this.removeItem(2)) {
                  interacting.setItemInHand(hand, spoon);
                  level.playSound(
                     (Player)null,
                     (double)((float)this.getBlockPos().getX() + 0.5F),
                     (double)this.getBlockPos().getY(),
                     (double)((float)this.getBlockPos().getZ() + 0.5F),
                     SoundEvents.LANTERN_HIT,
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
                  );
                  this.sendUpdate(level, pos, state);
                  return InteractionResult.SUCCESS;
               }

               if (this.removeItem(3)) {
                  interacting.setItemInHand(hand, knife);
                  level.playSound(
                     (Player)null,
                     (double)((float)this.getBlockPos().getX() + 0.5F),
                     (double)this.getBlockPos().getY(),
                     (double)((float)this.getBlockPos().getZ() + 0.5F),
                     SoundEvents.LANTERN_HIT,
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
                  );
                  this.sendUpdate(level, pos, state);
                  return InteractionResult.SUCCESS;
               }
            }
         } else {
            if (TrayItem.itemIsFood(itemInMainHand) && !this.hasFood()) {
               this.setItem(itemInMainHand.copyWithCount(1), 0);
               itemInMainHand.shrink(1);
               level.playSound(
                  (Player)null,
                  (double)((float)this.getBlockPos().getX() + 0.5F),
                  (double)this.getBlockPos().getY(),
                  (double)((float)this.getBlockPos().getZ() + 0.5F),
                  SoundEvents.COD_FLOP,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
               );
               this.sendUpdate(level, pos, state);
               return InteractionResult.SUCCESS;
            }

            if (TrayItem.itemIsFork(itemInMainHand) && !this.hasFork()) {
               this.setItem(itemInMainHand.copyWithCount(1), 1);
               itemInMainHand.shrink(1);
               level.playSound(
                  (Player)null,
                  (double)((float)this.getBlockPos().getX() + 0.5F),
                  (double)this.getBlockPos().getY(),
                  (double)((float)this.getBlockPos().getZ() + 0.5F),
                  SoundEvents.LANTERN_HIT,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
               );
               this.sendUpdate(level, pos, state);
               return InteractionResult.SUCCESS;
            }

            if (TrayItem.itemIsSpoon(itemInMainHand) && !this.hasSpoon()) {
               this.setItem(itemInMainHand.copyWithCount(1), 2);
               itemInMainHand.shrink(1);
               level.playSound(
                  (Player)null,
                  (double)((float)this.getBlockPos().getX() + 0.5F),
                  (double)this.getBlockPos().getY(),
                  (double)((float)this.getBlockPos().getZ() + 0.5F),
                  SoundEvents.LANTERN_HIT,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
               );
               this.sendUpdate(level, pos, state);
               return InteractionResult.SUCCESS;
            }

            if (TrayItem.itemIsKnife(itemInMainHand) && !this.hasKnife()) {
               this.setItem(itemInMainHand.copyWithCount(1), 3);
               itemInMainHand.shrink(1);
               level.playSound(
                  (Player)null,
                  (double)((float)this.getBlockPos().getX() + 0.5F),
                  (double)this.getBlockPos().getY(),
                  (double)((float)this.getBlockPos().getZ() + 0.5F),
                  SoundEvents.LANTERN_HIT,
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
               );
               this.sendUpdate(level, pos, state);
               return InteractionResult.SUCCESS;
            }
         }
      }

      return InteractionResult.FAIL;
   }

   public void eat(Level level, Player player, ItemStack stack, float nutritionRatio) {
      FoodProperties foodproperties = (FoodProperties)stack.get(DataComponents.FOOD);
      if (foodproperties != null) {
         player.getFoodData().eat(Mth.floor((float)foodproperties.nutrition() * nutritionRatio), foodproperties.saturation());
         player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
         level.playSound(
            (Player)null,
            (double)this.getBlockPos().getX(),
            (double)this.getBlockPos().getY(),
            (double)this.getBlockPos().getZ(),
            SoundEvents.PLAYER_BURP,
            SoundSource.PLAYERS,
            0.5F,
            level.random.nextFloat() * 0.1F + 0.9F
         );
         if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
         }

         for (PossibleEffect possibleEffect : foodproperties.effects()) {
            if (!level.isClientSide && possibleEffect.effect() != null && level.random.nextFloat() < possibleEffect.probability()) {
               player.addEffect(new MobEffectInstance(possibleEffect.effect()));
            }
         }

         level.playSound(
            (Player)null,
            (double)((float)this.getBlockPos().getX() + 0.5F),
            (double)this.getBlockPos().getY(),
            (double)((float)this.getBlockPos().getZ() + 0.5F),
            player.getEatingSound(stack),
            SoundSource.NEUTRAL,
            1.0F,
            1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F
         );
         this.removeItem(0);
         player.gameEvent(GameEvent.EAT);
      }
   }

   @Nonnull
   public ItemStack getFoodStack() {
      for (ItemStack itemStack : this.items) {
         if (TrayItem.itemIsFood(itemStack)) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public ItemStack getForkStack() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.FORK.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public ItemStack getSpoonStack() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.SPOON.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public ItemStack getKnifeStack() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.KNIFE.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean addItem(@Nonnull ItemStack stack) {
      for (int i = 0; i < this.items.size(); i++) {
         if (((ItemStack)this.items.get(i)).isEmpty()) {
            this.items.set(i, stack);
            return true;
         }
      }

      return false;
   }

   protected boolean setItem(@Nonnull ItemStack stack, int index) {
      if (((ItemStack)this.items.get(index)).isEmpty()) {
         this.items.set(index, stack);
         return true;
      } else {
         return false;
      }
   }

   protected boolean removeItem(int index) {
      if (index >= 0 && index < this.items.size()) {
         boolean removedSomething = !((ItemStack)this.items.get(index)).isEmpty();
         this.items.set(index, ItemStack.EMPTY);
         return removedSomething;
      } else {
         return false;
      }
   }

   protected void sendUpdate(Level level, BlockPos pos, BlockState state) {
      setChanged(level, pos, state);
      level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void loadFromItem(@Nonnull ItemStack stack, Level level, BlockPos pos, BlockState state) {
      this.items = TrayItem.getContents(stack);
      this.sendUpdate(level, pos, state);
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.saveAdditional(tag, provider);
      tag.put("Items", TrayItem.saveItemToTagList(this.items));
   }

   protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.loadAdditional(tag, provider);
      if (tag.contains("Items")) {
         this.items = TrayItem.getContents(tag.getList("Items", 10));
      }
   }

   public void dropItem(@Nonnull Level level, @Nonnull BlockPos pos) {
      ItemStack i = TrayItem.createTrayFrom(this.items);
      double d0 = (double)EntityType.ITEM.getWidth();
      double d1 = 0.2;
      double d2 = d0 / 2.0;
      double d3 = Math.floor((double)pos.getX()) + level.random.nextDouble() * d1 + d2;
      double d4 = Math.floor((double)pos.getY()) + level.random.nextDouble() * d1;
      double d5 = Math.floor((double)pos.getZ()) + level.random.nextDouble() * d1 + d2;
      ItemEntity e = new ItemEntity(level, d3, d4, d5, i);
      e.setDeltaMovement(
         level.random.triangle(0.0, 0.11485000171139836), level.random.triangle(0.2, 0.11485000171139836), level.random.triangle(0.0, 0.11485000171139836)
      );
      e.setDefaultPickUpDelay();
      level.addFreshEntity(e);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(@Nonnull Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, @Nonnull Provider provider) {
      super.onDataPacket(net, pkt, provider);
   }

   protected boolean hasFood() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.get(DataComponents.FOOD) != null) {
            return true;
         }
      }

      return false;
   }

   protected boolean hasSpoon() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.SPOON.get())) {
            return true;
         }
      }

      return false;
   }

   protected boolean hasFork() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.FORK.get())) {
            return true;
         }
      }

      return false;
   }

   protected boolean hasKnife() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.KNIFE.get())) {
            return true;
         }
      }

      return false;
   }

   protected ItemStack getFood() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.get(DataComponents.FOOD) != null) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected ItemStack getFork() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.FORK.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected ItemStack getSpoon() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.SPOON.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected ItemStack getKnife() {
      for (ItemStack itemStack : this.items) {
         if (itemStack.is((Item)ModItems.KNIFE.get())) {
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }
}
