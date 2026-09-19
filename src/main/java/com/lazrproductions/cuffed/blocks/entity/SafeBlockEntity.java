package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.blocks.SafeBlock;
import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModSounds;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SafeBlockEntity extends RandomizableContainerBlockEntity {
   private UUID lockId;
   private boolean locked;
   private boolean hasBeenBound;
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
         SafeBlockEntity.this.playSound(p_155064_, ModSounds.SAFE_OPEN);
         SafeBlockEntity.this.updateBlockState(p_155064_, true);
      }

      protected void onClose(Level p_155072_, BlockPos p_155073_, BlockState p_155074_) {
         SafeBlockEntity.this.playSound(p_155074_, ModSounds.SAFE_CLOSE);
         SafeBlockEntity.this.updateBlockState(p_155074_, false);
      }

      protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
      }

      protected boolean isOwnContainer(Player p_155060_) {
         if (p_155060_.containerMenu instanceof ChestMenu) {
            Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
            return container == SafeBlockEntity.this;
         } else {
            return false;
         }
      }
   };
   private Component customName;

   public SafeBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.SAFE_BLOCK_ENTITY.get(), pos, state);
      this.lockId = UUID.randomUUID();
      this.locked = false;
      this.hasBeenBound = false;
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      if (!this.trySaveLootTable(tag)) {
         ContainerHelper.saveAllItems(tag, this.items, provider);
      }

      tag.putUUID("LockId", this.lockId);
      tag.putBoolean("Locked", this.locked);
      tag.putBoolean("HasBeenBound", this.hasBeenBound);
      if (this.customName != null) {
         tag.putString("CustomName", Serializer.toJson(this.customName, provider));
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, provider);
      }

      this.lockId = tag.getUUID("LockId");
      this.locked = tag.getBoolean("Locked");
      this.hasBeenBound = tag.getBoolean("HasBeenBound");
      if (tag.contains("CustomName", 8)) {
         this.customName = Serializer.fromJson(tag.getString("CustomName"), provider);
      }
   }

   public int getContainerSize() {
      return 27;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> newItems) {
      this.items = newItems;
   }

   protected Component getDefaultName() {
      return Component.translatable("block.cuffed.safe");
   }

   public Component getName() {
      return this.customName != null ? this.customName : this.getDefaultName();
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   public void setCustomName(Component name) {
      this.customName = name;
   }

   protected AbstractContainerMenu createMenu(int menuId, Inventory inventory) {
      return ChestMenu.threeRows(menuId, inventory, this);
   }

   public void startOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void stopOpen(Player player) {
      if (!this.remove && !player.isSpectator()) {
         this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   public void setLocked(boolean value, Level level, Player player, BlockPos pos) {
      level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
      player.displayClientMessage(Component.translatable("info.lock.toggle_" + (!this.locked ? "on" : "off")), true);
      this.locked = value;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public UUID getLockId() {
      return this.lockId;
   }

   public boolean hasBeenBound() {
      return this.hasBeenBound;
   }

   public void bind() {
      this.hasBeenBound = true;
   }

   public void resetBinding() {
      this.lockId = UUID.randomUUID();
      this.locked = false;
      this.hasBeenBound = false;
   }

   void updateBlockState(BlockState state, boolean b1) {
      this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue(SafeBlock.OPEN, b1), 3);
   }

   void playSound(BlockState state, SoundEvent soundEvent) {
      Vec3i vec3i = ((Direction)state.getValue(SafeBlock.FACING)).getNormal();
      double d0 = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
      double d1 = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
      double d2 = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
      this.level.playSound((Player)null, d0, d1, d2, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
   }
}
