package com.lazrproductions.cuffed.api;

import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.blocks.base.ILockableBlock;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.client.gui.screen.LockpickingScreen;
import com.lazrproductions.cuffed.entity.PadlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.packet.LockpickBlockPacket;
import com.lazrproductions.cuffed.packet.LockpickLockPacket;
import com.lazrproductions.cuffed.packet.LockpickRestraintPacket;
import com.lazrproductions.cuffed.packet.RestraintEquippedPacket;
import com.lazrproductions.cuffed.packet.RestraintSyncPacket;
import com.lazrproductions.cuffed.packet.RestraintUtilityPacket;
import com.lazrproductions.cuffed.restraints.base.AbstractRestraint;
import com.lazrproductions.cuffed.restraints.base.RestraintType;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class CuffedAPI {
   public static class Capabilities {
      public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "cuffed");
      public static final Supplier<AttachmentType<RestrainableCapability>> RESTRAINABLE_ATTACHMENT = ATTACHMENT_TYPES.register(
         "restrainable", () -> AttachmentType.serializable(RestrainableCapability::new).copyOnDeath().build()
      );

      public static IRestrainableCapability getRestrainableCapability(Player player) {
         return (IRestrainableCapability)player.getData(RESTRAINABLE_ATTACHMENT.get());
      }
   }

   public static class Lockpicking {
      public static final HashMap<BlockPos, UUID> registeredLocks = new HashMap<>();

      public static void finishLockpickingLock(boolean wasFailed, int lockId, @Nonnull UUID lockpickerUUID) {
         ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
         if (player != null) {
            Level level = player.level();
            if (level != null && !level.isClientSide()) {
               ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
               player.getCooldowns().addCooldown((Item)ModItems.LOCKPICK.get(), 20);
               if (wasFailed) {
                  itemstack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                  player.awardStat((ResourceLocation)ModStatistics.LOCKPICKS_BROKEN.get());
               } else {
                  level.playLocalSound(
                     (double)((float)player.position().x),
                     (double)((float)player.position().y),
                     (double)((float)player.position().z),
                     SoundEvents.CHAIN_BREAK,
                     SoundSource.PLAYERS,
                     1.0F,
                     1.0F,
                     true
                  );
                  itemstack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                  if (level.getEntity(lockId) instanceof PadlockEntity e) {
                     player.awardStat((ResourceLocation)ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                     e.RemoveLock();
                  }
               }
            }
         }
      }

      public static void finishLockpickingRestraint(
         boolean wasFailed, RestraintType restraintType, @Nonnull UUID restrainedPlayerUUID, @Nonnull UUID lockpickerUUID
      ) {
         ServerPlayer lockpicker = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
         ServerPlayer restrained = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(restrainedPlayerUUID);
         if (lockpicker != null && restrained != null) {
            Level level = lockpicker.level();
            if (level != null && !level.isClientSide()) {
               ItemStack itemstack = lockpicker.getItemInHand(InteractionHand.MAIN_HAND);
               lockpicker.getCooldowns().addCooldown((Item)ModItems.LOCKPICK.get(), 20);
               if (wasFailed) {
                  itemstack.hurtAndBreak(1, lockpicker, EquipmentSlot.MAINHAND);
                  lockpicker.awardStat((ResourceLocation)ModStatistics.LOCKPICKS_BROKEN.get());
               } else {
                  level.playLocalSound(
                     (double)((float)restrained.position().x),
                     (double)((float)restrained.position().y),
                     (double)((float)restrained.position().z),
                     SoundEvents.CHAIN_BREAK,
                     SoundSource.PLAYERS,
                     1.0F,
                     1.0F,
                     true
                  );
                  itemstack.hurtAndBreak(1, lockpicker, EquipmentSlot.MAINHAND);
                  lockpicker.awardStat((ResourceLocation)ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                  RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(restrained);
                  cap.TryUnequipRestraint(restrained, lockpicker, restraintType);
               }
            }
         }
      }

      public static void finishLockpickingCellDoor(boolean wasFailed, @Nonnull BlockPos pos, UUID lockpickerUUID) {
         ServerPlayer lockpicker = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(lockpickerUUID);
         if (lockpicker != null) {
            Level level = lockpicker.level();
            if (level != null && !level.isClientSide()) {
               ItemStack itemstack = lockpicker.getItemInHand(InteractionHand.MAIN_HAND);
               lockpicker.getCooldowns().addCooldown((Item)ModItems.LOCKPICK.get(), 20);
               if (wasFailed) {
                  itemstack.hurtAndBreak(1, lockpicker, EquipmentSlot.MAINHAND);
                  lockpicker.awardStat((ResourceLocation)ModStatistics.LOCKPICKS_BROKEN.get());
               } else {
                  level.playLocalSound(
                     (double)((float)lockpicker.position().x),
                     (double)((float)lockpicker.position().y),
                     (double)((float)lockpicker.position().z),
                     SoundEvents.CHAIN_BREAK,
                     SoundSource.PLAYERS,
                     1.0F,
                     1.0F,
                     true
                  );
                  lockpicker.awardStat((ResourceLocation)ModStatistics.SUCCESSFUL_LOCKPICKS.get());
                  itemstack.hurtAndBreak(1, lockpicker, EquipmentSlot.MAINHAND);
                  level.destroyBlock(pos, true);
               }
            }
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static void beginLockpickingLock(@Nonnull Minecraft instance, int lockId, int speedIncreasePerPhase, int progressPerPick) {
         LockpickingScreen overlay = new LockpickingScreen(instance);
         overlay.speedIncreasePerPhase = speedIncreasePerPhase;
         overlay.progressPerPick = progressPerPick;
         overlay.type = 0;
         overlay.lockId = lockId;
         instance.setScreen(overlay);
      }

      @OnlyIn(Dist.CLIENT)
      public static void beginLockpickingRestraint(
         @Nonnull Minecraft instance, String restrainedUUID, int restraintType, int speedIncreasePerPhase, int progressPerPick
      ) {
         LockpickingScreen overlay = new LockpickingScreen(instance);
         overlay.speedIncreasePerPhase = speedIncreasePerPhase;
         overlay.progressPerPick = progressPerPick;
         overlay.type = 1;
         overlay.restrainedUUID = restrainedUUID;
         overlay.restraintType = restraintType;
         instance.setScreen(overlay);
      }

      @OnlyIn(Dist.CLIENT)
      public static void beginLockpickingCellDoor(@Nonnull Minecraft instance, BlockPos pos, int speedIncreasePerPhase, int progressPerPick) {
         LockpickingScreen overlay = new LockpickingScreen(instance);
         overlay.speedIncreasePerPhase = speedIncreasePerPhase;
         overlay.progressPerPick = progressPerPick;
         overlay.type = 2;
         overlay.doorPos = pos;
         instance.setScreen(overlay);
      }

      public static boolean isLockedAt(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos) {
         if (state.is(ModTags.Blocks.LOCKABLE_BLOCKS)) {
            PadlockEntity padlock = PadlockEntity.getLockAt(level, pos);
            if (padlock != null && padlock.isLocked()) {
               return true;
            }

            if (state.getBlock() instanceof ILockableBlock && ILockableBlock.isLocked(state)) {
               return true;
            }

            if (state.getBlock() instanceof DoorBlock door) {
               PadlockEntity eB = PadlockEntity.getLockAt(level, pos.below());
               PadlockEntity eA = PadlockEntity.getLockAt(level, pos.above());
               if (level.getBlockState(pos.below()).is(door) && eB != null && eB.isLocked()) {
                  return true;
               }

               if (level.getBlockState(pos.above()).is(door) && eA != null && eA.isLocked()) {
                  return true;
               }
            }

            if (state.getBlock() instanceof PilloryBlock pillory) {
               PadlockEntity eBx = PadlockEntity.getLockAt(level, pos.below());
               PadlockEntity eAx = PadlockEntity.getLockAt(level, pos.above());
               if (level.getBlockState(pos.below()).is(pillory) && eBx != null && eBx.isLocked()) {
                  return true;
               }

               if (level.getBlockState(pos.above()).is(pillory) && eAx != null && eAx.isLocked()) {
                  return true;
               }
            }

            if (state.getBlock() instanceof ChestBlock chest) {
               if (state.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
                  return false;
               }

               Direction dir = ChestBlock.getConnectedDirection(state);
               BlockPos otherPos = pos.relative(dir);
               PadlockEntity otherPadlock = PadlockEntity.getLockAt(level, otherPos);
               if (level.getBlockState(otherPos).is(Blocks.CHEST) && otherPadlock != null && otherPadlock.isLocked()) {
                  return true;
               }
            }
         }

         return false;
      }

      public static void loadWorld() {
         registeredLocks.clear();
      }

      public static void saveWorld() {
      }
   }

   public static class Networking {
      public static void sendRestraintSyncPacket(@Nonnull ServerPlayer client) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(client);
         RestraintSyncPacket packet = new RestraintSyncPacket(client.getId(), client.getUUID().toString(), cap.serializeNBT(client.registryAccess()));
         PacketDistributor.sendToPlayer(client, packet, new CustomPacketPayload[0]);
      }

      public static void sendRestraintEquipPacket(
         @Nonnull ServerPlayer client,
         @Nullable ServerPlayer captor,
         RestraintType type,
         @Nullable AbstractRestraint newRestraint,
         @Nullable AbstractRestraint oldRestraint
      ) {
         RestraintEquippedPacket packet = new RestraintEquippedPacket(
            client.getId(),
            client.getUUID().toString(),
            type,
            oldRestraint != null ? oldRestraint.serializeNBT(client.registryAccess()) : null,
            newRestraint != null ? newRestraint.serializeNBT(client.registryAccess()) : null,
            captor != null ? captor.getUUID().toString() : "null"
         );
         PacketDistributor.sendToPlayer(client, packet, new CustomPacketPayload[0]);
      }

      public static void sendRestraintUtilityPacketToClient(
         ServerPlayer client, RestraintType restraintType, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg
      ) {
         RestraintUtilityPacket packet = new RestraintUtilityPacket(restraintType.toInteger(), utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
         PacketDistributor.sendToPlayer(client, packet, new CustomPacketPayload[0]);
      }

      public static void sendRestraintUtilityPacketToServer(
         RestraintType restraintType, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg
      ) {
         RestraintUtilityPacket packet = new RestraintUtilityPacket(restraintType.toInteger(), utiltiyCode, integerArg, booleanArg, doubleArg, stringArg);
         PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickFinishPickingLockPacketToServer(boolean wasFailed, int lockId, UUID playerUUID) {
         LockpickLockPacket packet = new LockpickLockPacket(wasFailed, lockId, playerUUID.toString());
         PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickFinishPickingRestraintPacketToServer(boolean wasFailed, String restrainedUUID, int restraintType, UUID playerUUID) {
         LockpickRestraintPacket packet = new LockpickRestraintPacket(wasFailed, restrainedUUID, restraintType, playerUUID.toString());
         PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickFinishPickingCellDoorPacketToServer(boolean wasFailed, BlockPos pos, UUID playerUUID) {
         LockpickBlockPacket packet = new LockpickBlockPacket(wasFailed, pos, playerUUID.toString());
         PacketDistributor.sendToServer(packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickBeginPickingLockPacketToClient(@Nonnull ServerPlayer player, int lockId, int speedIncreasePerPhase, int progressPerPick) {
         LockpickLockPacket packet = new LockpickLockPacket(lockId, speedIncreasePerPhase, progressPerPick, player.getUUID().toString());
         PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickBeginPickingRestraintPacketToClient(
         @Nonnull ServerPlayer player, String restrainedUUID, int restraintType, int speedIncreasePerPhase, int progressPerPick
      ) {
         LockpickRestraintPacket packet = new LockpickRestraintPacket(
            restrainedUUID, restraintType, speedIncreasePerPhase, progressPerPick, player.getUUID().toString()
         );
         PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
      }

      public static void sendLockpickBeginPickingCellDoorPacketToClient(
         @Nonnull ServerPlayer player, BlockPos pos, int speedIncreasePerPhase, int progressPerPick
      ) {
         LockpickBlockPacket packet = new LockpickBlockPacket(pos, speedIncreasePerPhase, progressPerPick, player.getUUID().toString());
         PacketDistributor.sendToPlayer(player, packet, new CustomPacketPayload[0]);
      }

      public static void registerPackets() {
      }
   }

   public static class Privacy {
   }
}
