package com.lazrproductions.cuffed.restraints.base;

import net.minecraft.client.Options;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

public interface IBreakableRestraint {
   SoundEvent getBreakSound();

   int getMaxDurability();

   boolean isKeyToAttemptBreak(int var1, Options var2);

   boolean requireAlternateKeysToAttemptBreak();

   boolean dropItemOnBroken();

   boolean canBeBrokenOutOf();

   int getDurability();

   void attemptToBreak(Player var1, int var2, int var3, Options var4);

   void setDurability(ServerPlayer var1, int var2);

   void incrementDurability(ServerPlayer var1, int var2);

   void onBrokenServer(ServerPlayer var1);

   void onBrokenClient(Player var1);
}
