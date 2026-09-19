package com.lazrproductions.cuffed;

import com.lazrproductions.cuffed.packet.LockpickBlockPacket;
import com.lazrproductions.cuffed.packet.LockpickLockPacket;
import com.lazrproductions.cuffed.packet.LockpickRestraintPacket;
import com.lazrproductions.cuffed.packet.RestraintEquippedPacket;
import com.lazrproductions.cuffed.packet.RestraintSyncPacket;
import com.lazrproductions.cuffed.packet.RestraintUtilityPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ServerPayloadRegister {
   public static void register(PayloadRegistrar registrar) {
      registrar.playToClient(RestraintSyncPacket.TYPE, RestraintSyncPacket.STREAM_CODEC, (payload, context) -> {
      });
      registrar.playToClient(RestraintEquippedPacket.TYPE, RestraintEquippedPacket.STREAM_CODEC, (payload, context) -> {
      });
      registrar.playBidirectional(RestraintUtilityPacket.TYPE, RestraintUtilityPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isServerbound()) {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickBlockPacket.TYPE, LockpickBlockPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isServerbound()) {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickLockPacket.TYPE, LockpickLockPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isServerbound()) {
            payload.handleServer(context);
         }
      });
      registrar.playBidirectional(LockpickRestraintPacket.TYPE, LockpickRestraintPacket.STREAM_CODEC, (payload, context) -> {
         if (context.flow().isServerbound()) {
            payload.handleServer(context);
         }
      });
   }
}
