package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.init.ModBlockEntities;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LockableBlockEntity extends BlockEntity {
   private UUID lockId = UUID.randomUUID();
   private boolean locked = false;
   private String lockName;
   private boolean hasBeenBound;

   public LockableBlockEntity(BlockPos pos, BlockState state, String lockName) {
      super((BlockEntityType)ModBlockEntities.CELL_DOOR_BLOCK_ENTITY.get(), pos, state);
      this.lockName = lockName;
      this.hasBeenBound = false;
   }

   public LockableBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.CELL_DOOR_BLOCK_ENTITY.get(), pos, state);
      this.lockName = "info.cuffed.lock";
      this.hasBeenBound = false;
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putUUID("LockId", this.lockId);
      tag.putBoolean("Locked", this.locked);
      tag.putString("LockName", this.lockName);
      tag.putBoolean("HasBeenBound", this.hasBeenBound);
   }

   protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.loadAdditional(tag, provider);
      this.lockId = tag.getUUID("LockId");
      this.locked = tag.getBoolean("Locked");
      this.lockName = tag.getString("LockName");
      this.hasBeenBound = tag.getBoolean("HasBeenBound");
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

   public String getLockName() {
      return this.lockName;
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
}
