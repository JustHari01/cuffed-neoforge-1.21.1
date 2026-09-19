package com.lazrproductions.cuffed.client;

import com.lazrproductions.cuffed.packet.LockpickBlockPacket;
import com.lazrproductions.cuffed.packet.LockpickLockPacket;
import com.lazrproductions.cuffed.packet.LockpickRestraintPacket;
import com.lazrproductions.cuffed.packet.RestraintEquippedPacket;
import com.lazrproductions.cuffed.packet.RestraintSyncPacket;
import com.lazrproductions.cuffed.packet.RestraintUtilityPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ClientPayloadRegister {
   public static void register(PayloadRegistrar registrar) {
      registrar.playToClient(RestraintSyncPacket.TYPE, RestraintSyncPacket.STREAM_CODEC, ClientPacketHandlers::handleRestraintSync);
      registrar.playToClient(RestraintEquippedPacket.TYPE, RestraintEquippedPacket.STREAM_CODEC, ClientPacketHandlers::handleRestraintEquipped);
      registrar.playBidirectional(RestraintUtilityPacket.TYPE, RestraintUtilityPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isClientbound()) {
            ClientPacketHandlers.handleRestraintUtility(payload, context);
         } else {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickBlockPacket.TYPE, LockpickBlockPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isClientbound()) {
            ClientPacketHandlers.handleLockpickBlock(payload, context);
         } else {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickLockPacket.TYPE, LockpickLockPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isClientbound()) {
            ClientPacketHandlers.handleLockpickLock(payload, context);
         } else {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickRestraintPacket.TYPE, LockpickRestraintPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isClientbound()) {
            ClientPacketHandlers.handleLockpickRestraint(payload, context);
         } else {
            payload.handleServer(context);
         }
      });
   }
}
