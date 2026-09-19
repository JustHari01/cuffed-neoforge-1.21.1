package com.lazrproductions.cuffed.compat;

import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;

public class KnightsOfBritanniaCompat {
   public static void load() {
   }

   public static void DrainMana(@Nonnull ServerPlayer player, int amount) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         ServerScoreboard scoreboard = server.getScoreboard();
         if (scoreboard != null) {
            Objective currentManaObjective = scoreboard.getObjective("kob.mana");
            if (currentManaObjective != null) {
               ScoreAccess myManaScore = scoreboard.getOrCreatePlayerScore(player, currentManaObjective);
               int currentMana = myManaScore.get();
               if (currentMana > 0) {
                  myManaScore.set(currentMana - amount);
               }
            }
         }
      }
   }

   public static void DrainMana(@Nonnull ServerPlayer player, double amountPercentage) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         ServerScoreboard scoreboard = server.getScoreboard();
         if (scoreboard != null) {
            Objective currentManaObjective = scoreboard.getObjective("kob.mana");
            if (currentManaObjective != null) {
               Objective maxManaObjective = scoreboard.getObjective("kob.mana.max");
               if (maxManaObjective != null) {
                  ScoreAccess myManaScore = scoreboard.getOrCreatePlayerScore(player, currentManaObjective);
                  ScoreAccess maxManaScore = scoreboard.getOrCreatePlayerScore(player, maxManaObjective);
                  int currentMana = myManaScore.get();
                  if (currentMana > 0) {
                     float amount = (float)maxManaScore.get() * (float)amountPercentage;
                     myManaScore.set(currentMana - (int)amount);
                  }
               }
            }
         }
      }
   }
}
