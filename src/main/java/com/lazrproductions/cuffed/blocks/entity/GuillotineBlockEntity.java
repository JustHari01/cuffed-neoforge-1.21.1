package com.lazrproductions.cuffed.blocks.entity;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.compat.PlayerReviveCompat;
import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModDamageTypes;
import com.lazrproductions.cuffed.init.ModSounds;
import com.lazrproductions.cuffed.utils.ItemTagUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GuillotineBlockEntity extends BlockEntity {
   static final String TAG_IS_DOWN = "IsDown";
   static final String TAG_IS_BLOODY = "IsBloody";
   public boolean isBloody;
   public boolean isDown;
   int chopDelay = 0;

   public GuillotineBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.GUILLOTINE.get(), pos, state);
   }

   public void interact(@Nonnull Level l, @Nonnull BlockPos pos, @Nonnull BlockState state) {
      if (!l.isClientSide()) {
         this.isDown = !this.isDown;
         l.playSound(null, pos, this.isDown ? ModSounds.GUILLOTINE_USE : ModSounds.PILLORY_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (this.isDown) {
            this.chopDelay = 5;
         }

         setChanged(l, pos, state);
         l.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public void chop(@Nonnull Level l, @Nonnull BlockPos pos, @Nonnull BlockState state) {
      Player player = PilloryBlock.getDetainedEntity(l, state, pos);
      if (player != null) {
         if (CuffedMod.PlayerReviveInstalled) {
            PlayerReviveCompat.Kill(player);
         } else {
            player.kill();
         }

         player.hurt(ModDamageTypes.GetModSource(player, ModDamageTypes.HANG, null), Float.MAX_VALUE);
         if ((Boolean)CuffedMod.SERVER_CONFIG.GUILLOTINE_DROPS_HEAD.get()) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD, 1);
            ItemTagUtils.updateTag(stack, t -> t.putString("SkullOwner", player.getGameProfile().getName()));
            ItemEntity entity = new ItemEntity(l, player.position().x(), player.position().y(), player.position().z(), stack);
            l.addFreshEntity(entity);
         }

         this.isBloody = true;
      }
   }

   public void tick(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
      if (this.chopDelay > 0) {
         this.chopDelay--;
         if (this.chopDelay == 0) {
            this.chop(level, pos, state);
         }
      }
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putBoolean("IsDown", this.isDown);
      tag.putBoolean("IsBloody", this.isBloody);
   }

   protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull Provider provider) {
      super.loadAdditional(tag, provider);
      this.isDown = tag.getBoolean("IsDown");
      this.isBloody = tag.getBoolean("IsBloody");
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
}
