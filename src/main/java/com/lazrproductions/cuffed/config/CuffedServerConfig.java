package com.lazrproductions.cuffed.config;

import com.lazrproductions.lazrslib.common.config.ConfigCategory;
import com.lazrproductions.lazrslib.common.config.ConfigProperty;
import com.lazrproductions.lazrslib.common.config.LazrConfig;
import net.neoforged.fml.config.ModConfig.Type;

public class CuffedServerConfig extends LazrConfig {
   public ConfigProperty<Integer> MAX_KEYS_PER_RING;
   public ConfigProperty<Integer> SAFE_SLOTS;
   public ConfigProperty<Boolean> REQUIRE_LOW_HEALTH_TO_RESTRAIN;
   public ConfigProperty<Boolean> ALLOW_BREAKING_OUT_OF_PILLORY;
   public ConfigProperty<Boolean> GUILLOTINE_DROPS_HEAD;
   public ConfigProperty<Boolean> ALLOW_SELF_RESTRAINING;
   public ConfigCategory ANCHORING_SETTINGS;
   public ConfigProperty<Boolean> ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED;
   public ConfigProperty<Float> ANCHORING_MAX_CHAIN_LENGTH;
   public ConfigProperty<Float> ANCHORING_SUFFOCATION_LENGTH;
   public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_FENCES;
   public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS;
   public ConfigProperty<Boolean> ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS;
   public ConfigCategory NICKNAME_SETTINGS;
   public ConfigProperty<Boolean> NICKNAME_PERSISTS_ON_DEATH;
   public ConfigProperty<Boolean> NICKNAME_PERSISTS_ON_LOGOUT;
   public ConfigCategory LOCKPICKING_SETTINGS;
   public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_PADLOCKS;
   public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_PADLOCKS;
   public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS;
   public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS;
   public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_CELL_DOORS;
   public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_CELL_DOORS;
   public ConfigProperty<Integer> LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_SAFES;
   public ConfigProperty<Integer> LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_SAFES;
   public ConfigCategory RESTRAINT_DURABILITY_SETTINGS;
   public ConfigProperty<Integer> RESTRAINT_DURABILITY_HANDCUFFS;
   public ConfigProperty<Integer> RESTRAINT_DURABILITY_FUZZY_HANDCUFFS;
   public ConfigProperty<Integer> RESTRAINT_DURABILITY_SHACKLES;
   public ConfigCategory HANDCUFFS_ON_ARMS_SETTINGS;
   public ConfigProperty<Boolean> HANDCUFFS_ON_ARMS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> HANDCUFFS_ON_ARMS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Boolean> HANDCUFFS_ON_ARMS_LOCKPICKABLE;
   public ConfigProperty<Integer> HANDCUFFS_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK;
   public ConfigProperty<Integer> HANDCUFFS_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK;
   public ConfigCategory FUZZY_HANDCUFFS_ON_ARMS_SETTINGS;
   public ConfigProperty<Boolean> FUZZY_HANDCUFFS_ON_ARMS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> FUZZY_HANDCUFFS_ON_ARMS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Boolean> FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKABLE;
   public ConfigProperty<Integer> FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK;
   public ConfigProperty<Integer> FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK;
   public ConfigCategory SHACKLES_ON_ARMS_SETTINGS;
   public ConfigProperty<Boolean> SHACKLES_ON_ARMS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> SHACKLES_ON_ARMS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Boolean> SHACKLES_ON_ARMS_LOCKPICKABLE;
   public ConfigProperty<Integer> SHACKLES_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK;
   public ConfigProperty<Integer> SHACKLES_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK;
   public ConfigCategory DUCK_TAPE_ON_ARMS_SETTINGS;
   public ConfigProperty<Boolean> DUCK_TAPE_ON_ARMS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> DUCK_TAPE_ON_ARMS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Integer> DUCK_TAPE_ON_ARMS_DURABILITY;
   public ConfigCategory HANDCUFFS_ON_LEGS_SETTINGS;
   public ConfigProperty<Boolean> HANDCUFFS_ON_LEGS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> HANDCUFFS_ON_LEGS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Boolean> HANDCUFFS_ON_LEGS_LOCKPICKABLE;
   public ConfigProperty<Integer> HANDCUFFS_ON_LEGS_LOCKPICKING_PROGRESS_PER_PICK;
   public ConfigProperty<Integer> HANDCUFFS_ON_LEGS_LOCKPICKING_SPEED_INCREASE_PER_PICK;
   public ConfigCategory SHACKLES_ON_LEGS_SETTINGS;
   public ConfigProperty<Boolean> SHACKLES_ON_LEGS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> SHACKLES_ON_LEGS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Boolean> SHACKLES_ON_LEGS_LOCKPICKABLE;
   public ConfigProperty<Integer> SHACKLES_ON_LEGS_LOCKPICKING_PROGRESS_PER_PICK;
   public ConfigProperty<Integer> SHACKLES_ON_LEGS_LOCKPICKING_SPEED_INCREASE_PER_PICK;
   public ConfigCategory DUCK_TAPE_ON_LEGS_SETTINGS;
   public ConfigProperty<Boolean> DUCK_TAPE_ON_LEGS_CAN_BE_BROKEN_OUT_OF;
   public ConfigProperty<Boolean> DUCK_TAPE_ON_LEGS_DROP_ITEM_WHEN_BROKEN;
   public ConfigProperty<Integer> DUCK_TAPE_ON_LEGS_DURABILITY;

   public CuffedServerConfig(String name, Type type) {
      super(name, type);
   }

   public void registerProperties() {
      this.MAX_KEYS_PER_RING = this.createGenericProperty(
         new ConfigProperty(this, "Maximum Keys Per Ring", "The maximum number of keys that can fit on a single key ring.", 16)
      );
      this.SAFE_SLOTS = this.createGenericProperty(new ConfigProperty(this, "Safes Slots", "The total number of slots in the safe.", 36));
      this.REQUIRE_LOW_HEALTH_TO_RESTRAIN = this.createGenericProperty(
         new ConfigProperty(
            this,
            "Require Low Health To Restrain",
            "Whether or not to require players to be under 30% health to be restrained. If a player is already restrained then this setting doesn't take effect.",
            false
         )
      );
      this.ALLOW_BREAKING_OUT_OF_PILLORY = this.createGenericProperty(
         new ConfigProperty(this, "Allow breaking out of the pillory", "Whether or not to allow players to spam crouch to break out of pillories.", true)
      );
      this.GUILLOTINE_DROPS_HEAD = this.createGenericProperty(
         new ConfigProperty(this, "Guillote drops the player's head", "Whether or not the guillotine drops the player's head when they die in it.", true)
      );
      this.ALLOW_SELF_RESTRAINING = this.createGenericProperty(
         new ConfigProperty(this, "Allow restraining yourself", "Whether or not players can apply/remove their own head and leg restraints.", true)
      );
      this.ANCHORING_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Anchoring Settings"),
         c -> {
            this.ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED = c.putProperty(
               new ConfigProperty(
                  this, "Only Restrained Players Can Be Restrained", "Whether or not to require players to be restrained to get anchored.", false
               )
            );
            this.ANCHORING_MAX_CHAIN_LENGTH = c.putProperty(
               new ConfigProperty(this, "Max Chain Length", "The maximum length of the chain when anchoring.", 5.0F)
            );
            this.ANCHORING_SUFFOCATION_LENGTH = c.putProperty(
               new ConfigProperty(this, "Suffocation Length", "The distance when anchored entites start suffocating.", 12.0F)
            );
            this.ANCHORING_ALLOW_ANCHORING_TO_FENCES = c.putProperty(
               new ConfigProperty(this, "Allow Anchoring To Fences", "Whether or not players should be allowed to anchor entities to FENCES.", true)
            );
            this.ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS = c.putProperty(
               new ConfigProperty(
                  this, "Allow Anchoring To Tripwire Hook", "Whether or not players should be allowed to anchor entities to TRIPWIRE HOOKS.", true
               )
            );
            this.ANCHORING_ALLOW_ANCHORING_TO_WEIGHTED_ANCHORS = c.putProperty(
               new ConfigProperty(
                  this, "Allow Anchoring To Weighted Anchors", "Whether or not players should be allowed to anchor entities to WEIGHTED ANCHORS.", true
               )
            );
         }
      );
      this.NICKNAME_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Nickname Settings"),
         c -> {
            this.NICKNAME_PERSISTS_ON_DEATH = c.putProperty(
               new ConfigProperty(this, "Nickname Persists On Death", "Whether or not nicknames should persist on death.", true)
            );
            this.NICKNAME_PERSISTS_ON_LOGOUT = c.putProperty(
               new ConfigProperty(this, "Nickname Persists On Logout", "Whether or not nicknames should persist on logout.", true)
            );
         }
      );
      this.LOCKPICKING_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Lockpicking Settings"),
         c -> {
            this.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_PADLOCKS = c.putProperty(
               new ConfigProperty(
                  this, "Progress Per Pick For Breaking Padlocks", "How much progress is gained on a successfull pick when lockpicking a PADLOCK.", 8
               )
            );
            this.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_PADLOCKS = c.putProperty(
               new ConfigProperty(
                  this, "Speed Increase Per Pick For Breaking Padlocks", "How much the progress-loss speeds up per pick when lockpicking a PADLOCK.", 10
               )
            );
            this.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS = c.putProperty(
               new ConfigProperty(
                  this,
                  "Progress Per Pick For Breaking Reinforced Padlocks",
                  "How much progress is gained on a successfull pick when lockpicking a REINFORCED PADLOCK.",
                  6
               )
            );
            this.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_REINFORCED_PADLOCKS = c.putProperty(
               new ConfigProperty(
                  this,
                  "Speed Increase Per Pick For Breaking Reinforced Padlocks",
                  "How much the progress-loss speeds up per pick when lockpicking a REINFORCED PADLOCK.",
                  13
               )
            );
            this.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_CELL_DOORS = c.putProperty(
               new ConfigProperty(
                  this, "Progress Per Pick For Breaking Cell Doors", "How much progress is gained on a successfull pick when lockpicking a CELL DOOR.", 6
               )
            );
            this.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_CELL_DOORS = c.putProperty(
               new ConfigProperty(
                  this, "Speed Increase Per Pick For Breaking Cell Doors", "How much the progress-loss speeds up per pick when lockpicking a CELL DOOR.", 14
               )
            );
            this.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_SAFES = c.putProperty(
               new ConfigProperty(
                  this, "Progress Per Pick For Breaking Safes", "How much progress is gained on a successfull pick when lockpicking a SAFES.", 3
               )
            );
            this.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_SAFES = c.putProperty(
               new ConfigProperty(
                  this, "Speed Increase Per Pick For Breaking Safes", "How much the progress-loss speeds up per pick when lockpicking a SAFES.", 10
               )
            );
         }
      );
      this.RESTRAINT_DURABILITY_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Restraint Durabilities"),
         c -> {
            this.RESTRAINT_DURABILITY_HANDCUFFS = c.putProperty(
               new ConfigProperty(this, "Handcuffs Durability", "The amount of durability handcuffs have.", 40)
            );
            this.RESTRAINT_DURABILITY_FUZZY_HANDCUFFS = c.putProperty(
               new ConfigProperty(this, "Fuzzy Handcuffs Durability", "The amount of durability fuzzy handcuffs have.", 30)
            );
            this.RESTRAINT_DURABILITY_SHACKLES = c.putProperty(new ConfigProperty(this, "Shackles Durability", "The amount of durability shackles have.", 15));
         }
      );
      this.HANDCUFFS_ON_ARMS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Handcuffs when on Arms"),
         c -> {
            this.HANDCUFFS_ON_ARMS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.HANDCUFFS_ON_ARMS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", false)
            );
            this.HANDCUFFS_ON_ARMS_LOCKPICKABLE = c.putProperty(
               new ConfigProperty(this, "Lockpickable", "Whether or not this restraint is lockpickable.", true)
            );
            this.HANDCUFFS_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Progresss Per Pick", "The amount of progress gained per pick when lockpicking this restraint.", 6)
            );
            this.HANDCUFFS_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Speed Increase Per Pick", "The speed increase per pick when lockpicking this restraint.", 12)
            );
         }
      );
      this.FUZZY_HANDCUFFS_ON_ARMS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Fuzzy Handcuffs when on Arms"),
         c -> {
            this.FUZZY_HANDCUFFS_ON_ARMS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.FUZZY_HANDCUFFS_ON_ARMS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", false)
            );
            this.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKABLE = c.putProperty(
               new ConfigProperty(this, "Lockpickable", "Whether or not this restraint is lockpickable.", true)
            );
            this.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Progresss Per Pick", "The amount of progress gained per pick when lockpicking this restraint.", 6)
            );
            this.FUZZY_HANDCUFFS_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Speed Increase Per Pick", "The speed increase per pick when lockpicking this restraint.", 14)
            );
         }
      );
      this.SHACKLES_ON_ARMS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Shackles when on Arms"),
         c -> {
            this.SHACKLES_ON_ARMS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.SHACKLES_ON_ARMS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", false)
            );
            this.SHACKLES_ON_ARMS_LOCKPICKABLE = c.putProperty(new ConfigProperty(this, "Lockpickable", "Whether or not this restraint is lockpickable.", true));
            this.SHACKLES_ON_ARMS_LOCKPICKING_PROGRESS_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Progresss Per Pick", "The amount of progress gained per pick when lockpicking this restraint.", 8)
            );
            this.SHACKLES_ON_ARMS_LOCKPICKING_SPEED_INCREASE_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Speed Increase Per Pick", "The speed increase per pick when lockpicking this restraint.", 10)
            );
         }
      );
      this.DUCK_TAPE_ON_ARMS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Duck Tape when on Arms"),
         c -> {
            this.DUCK_TAPE_ON_ARMS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.DUCK_TAPE_ON_ARMS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", true)
            );
            this.DUCK_TAPE_ON_ARMS_DURABILITY = c.putProperty(
               new ConfigProperty(this, "Durability", "The total durability of this restraint when breaking out of it.", 5)
            );
         }
      );
      this.HANDCUFFS_ON_LEGS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Handcuffs when on Legs"),
         c -> {
            this.HANDCUFFS_ON_LEGS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.HANDCUFFS_ON_LEGS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", false)
            );
            this.HANDCUFFS_ON_LEGS_LOCKPICKABLE = c.putProperty(
               new ConfigProperty(this, "Lockpickable", "Whether or not this restraint is lockpickable.", true)
            );
            this.HANDCUFFS_ON_LEGS_LOCKPICKING_PROGRESS_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Progresss Per Pick", "The amount of progress gained per pick when lockpicking this restraint.", 6)
            );
            this.HANDCUFFS_ON_LEGS_LOCKPICKING_SPEED_INCREASE_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Speed Increase Per Pick", "The speed increase per pick when lockpicking this restraint.", 12)
            );
         }
      );
      this.SHACKLES_ON_LEGS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Shackles when on Legs"),
         c -> {
            this.SHACKLES_ON_LEGS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.SHACKLES_ON_LEGS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", false)
            );
            this.SHACKLES_ON_LEGS_LOCKPICKABLE = c.putProperty(new ConfigProperty(this, "Lockpickable", "Whether or not this restraint is lockpickable.", true));
            this.SHACKLES_ON_LEGS_LOCKPICKING_PROGRESS_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Progresss Per Pick", "The amount of progress gained per pick when lockpicking this restraint.", 8)
            );
            this.SHACKLES_ON_LEGS_LOCKPICKING_SPEED_INCREASE_PER_PICK = c.putProperty(
               new ConfigProperty(this, "Lockpicking Speed Increase Per Pick", "The speed increase per pick when lockpicking this restraint.", 10)
            );
         }
      );
      this.DUCK_TAPE_ON_LEGS_SETTINGS = this.createCategory(
         new ConfigCategory(this, "Duck Tape when on Legs"),
         c -> {
            this.DUCK_TAPE_ON_LEGS_CAN_BE_BROKEN_OUT_OF = c.putProperty(
               new ConfigProperty(this, "Can Be Broken Out Of", "Whether or not this restraint can be broken out of.", true)
            );
            this.DUCK_TAPE_ON_LEGS_DROP_ITEM_WHEN_BROKEN = c.putProperty(
               new ConfigProperty(this, "Drop Item When Broken", "Whether or not to drop the item when broken out of.", true)
            );
            this.DUCK_TAPE_ON_LEGS_DURABILITY = c.putProperty(
               new ConfigProperty(this, "Durability", "The total durability of this restraint when breaking out of it.", 5)
            );
         }
      );
   }

   public static class BreakableRestraintConfig extends ConfigCategory {
      public ConfigProperty<Boolean> canBeBrokenOutOf;
      public ConfigProperty<Boolean> dropItemWhenBroken;
      public ConfigProperty<Boolean> lockpickable;
      public ConfigProperty<Integer> lockpickingProgressPerPick;
      public ConfigProperty<Integer> lockpickingSpeedIncreasePerPick;

      public BreakableRestraintConfig(
         LazrConfig config,
         String name,
         boolean canBeBrokenOutOf,
         boolean dropItemWhenBroken,
         boolean lockpickable,
         int lockpickingProgressPerPick,
         int lockpickingSpeedIncreasePerPick
      ) {
         super(config, name);
         this.canBeBrokenOutOf = this.putProperty(
            new ConfigProperty(config, "Can Be Broken Out Of", "Whether or not " + name + " can be broken out of.", canBeBrokenOutOf)
         );
         this.dropItemWhenBroken = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", dropItemWhenBroken)
         );
         this.lockpickable = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickable)
         );
         this.lockpickingProgressPerPick = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickingProgressPerPick)
         );
         this.lockpickingSpeedIncreasePerPick = this.putProperty(
            new ConfigProperty(
               config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickingSpeedIncreasePerPick
            )
         );
      }
   }

   public static class BreakableWithDurabilityRestraintConfig extends ConfigCategory {
      public ConfigProperty<Boolean> canBeBrokenOutOf;
      public ConfigProperty<Integer> durability;
      public ConfigProperty<Boolean> dropItemWhenBroken;
      public ConfigProperty<Boolean> lockpickable;
      public ConfigProperty<Integer> lockpickingProgressPerPick;
      public ConfigProperty<Integer> lockpickingSpeedIncreasePerPick;

      public BreakableWithDurabilityRestraintConfig(
         LazrConfig config,
         String name,
         boolean canBeBrokenOutOf,
         int durability,
         boolean dropItemWhenBroken,
         boolean lockpickable,
         int lockpickingProgressPerPick,
         int lockpickingSpeedIncreasePerPick
      ) {
         super(config, name);
         this.canBeBrokenOutOf = this.putProperty(
            new ConfigProperty(config, "Can Be Broken Out Of", "Whether or not " + name + " can be broken out of.", canBeBrokenOutOf)
         );
         this.durability = this.putProperty(new ConfigProperty(config, "Durability", "The total durability of " + name, durability));
         this.dropItemWhenBroken = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", dropItemWhenBroken)
         );
         this.lockpickable = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickable)
         );
         this.lockpickingProgressPerPick = this.putProperty(
            new ConfigProperty(config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickingProgressPerPick)
         );
         this.lockpickingSpeedIncreasePerPick = this.putProperty(
            new ConfigProperty(
               config, "Drop Item When Broken", "Whether or not " + name + "s drop as item when broken out of.", lockpickingSpeedIncreasePerPick
            )
         );
      }
   }
}
