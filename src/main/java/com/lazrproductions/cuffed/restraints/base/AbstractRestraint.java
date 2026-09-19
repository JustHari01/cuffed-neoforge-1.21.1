package com.lazrproductions.cuffed.restraints.base;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.compat.ArsNouveauCompat;
import com.lazrproductions.cuffed.compat.IronsSpellsnSpellbooksCompat;
import com.lazrproductions.cuffed.compat.ManaAndArtificeCompat;
import com.lazrproductions.cuffed.entity.animation.ArmRestraintAnimationFlags;
import com.lazrproductions.cuffed.entity.animation.LegRestraintAnimationFlags;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.restraints.client.RestraintModelInterface;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class AbstractRestraint {
   private ServerPlayer s_player;
   private Player c_player;
   private CompoundTag itemData;
   @Nonnull
   private UUID captor;
   protected int clientSidedDurability = 100;

   public AbstractRestraint() {
      this.captor = UUID.randomUUID();
   }

   public AbstractRestraint(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      if (this instanceof IEnchantableRestraint enchantable) {
         enchantable.setEnchantments(stack.getEnchantments());
      }

      this.itemData = new CompoundTag();
      stack.save(player.registryAccess(), this.itemData);
      this.captor = captor.getUUID();
   }

   public abstract ResourceLocation getId();

   public abstract RestraintType getType();

   public abstract String getActionBarLabel();

   public abstract String getName();

   public abstract Item getItem();

   public abstract Item getKeyItem();

   public abstract boolean AllowItemUse();

   public abstract boolean AllowBreakingBlocks();

   public abstract boolean AllowMovement();

   public abstract boolean AllowJumping();

   public abstract boolean AllowSprinting();

   public abstract SoundEvent getEquipSound();

   public abstract SoundEvent getUnequipSound();

   public abstract boolean getLockpickable();

   public abstract int getLockpickingSpeedIncreasePerPick();

   public abstract int getLockpickingProgressPerPick();

   public abstract ArrayList<Integer> getBlockedKeyCodes();

   public abstract ArmRestraintAnimationFlags getArmAnimationFlags();

   public abstract LegRestraintAnimationFlags getLegAnimationFlags();

   public boolean canEquipRestraintItem(ItemStack stack, ServerPlayer player, ServerPlayer captor) {
      return true;
   }

   public Player getPlayer() {
      return (Player)(this.isClientSide() ? this.c_player : this.s_player);
   }

   @Nullable
   public Player getCaptor(@Nonnull ServerLevel level) {
      return level.getPlayerByUUID(this.captor);
   }

   public boolean isClientSide() {
      return this.s_player == null;
   }

   public void onTickServer(ServerPlayer player) {
      this.s_player = player;
      ModStatistics.awardTimeSpentRestrained(player, this);
      if (this instanceof IEnchantableRestraint e) {
         RegistryAccess regAccess = player.registryAccess();
         if (this.getEnchantLevelSafely(e, ModEnchantments.FAMINE, regAccess) >= 1 && !player.hasEffect(MobEffects.HUNGER)) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
         }

         if (this.getEnchantLevelSafely(e, ModEnchantments.SHROUD, regAccess) >= 1 && !player.hasEffect(MobEffects.BLINDNESS)) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));
         }

         if (this.getEnchantLevelSafely(e, ModEnchantments.EXHAUST, regAccess) >= 1) {
            if (!player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
               player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
            }

            if (!player.hasEffect(MobEffects.WEAKNESS)) {
               player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            }
         }

         if (this.getEnchantLevelSafely(e, ModEnchantments.SILENCE, regAccess) >= 1) {
            double drainPercentage = 0.005;
            if (CuffedMod.ArsNouveauInstalled) {
               ArsNouveauCompat.DrainMana(player, drainPercentage);
            }

            if (CuffedMod.IronsSpellsnSpellbooksInstalled) {
               IronsSpellsnSpellbooksCompat.DrainMana(player, drainPercentage);
            }

            if (CuffedMod.ManaAndArtificeInstalled) {
               ManaAndArtificeCompat.DrainMana(player, drainPercentage);
            }
         }
      }
   }

   public void onTickClient(Player player) {
      this.c_player = player;
   }

   public void onEquippedServer(ServerPlayer player, ServerPlayer captor) {
      this.s_player = player;
      this.captor = captor.getUUID();
      ModStatistics.awardRestrained(player, this);
      Random random = new Random();
      player.level().playSound(null, player.blockPosition(), this.getEquipSound(), SoundSource.PLAYERS, 0.8F, random.nextFloat() * 0.2F + 0.9F);
   }

   public void onEquippedClient(Player player, Player captor) {
      this.c_player = player;
   }

   public void onUnequippedServer(ServerPlayer player) {
      Random random = new Random();
      player.level().playSound(null, player.blockPosition(), this.getUnequipSound(), SoundSource.PLAYERS, 0.8F, random.nextFloat() * 0.2F + 0.9F);
   }

   public void onUnequippedClient(Player player) {
   }

   public abstract void onLoginServer(ServerPlayer var1);

   public abstract void onLoginClient(Player var1);

   public abstract void onLogoutServer(ServerPlayer var1);

   public abstract void onLogoutClient(Player var1);

   public abstract void onDeathServer(ServerPlayer var1);

   public abstract void onDeathClient(Player var1);

   public abstract void onJumpServer(ServerPlayer var1);

   public abstract void onJumpClient(Player var1);

   public abstract float onLandServer(ServerPlayer var1, float var2, float var3);

   public abstract void onLandClient(Player var1, float var2, float var3);

   public abstract void renderOverlay(Player var1, GuiGraphics var2, float var3, Window var4);

   public void onKeyInput(Player player, int keyCode, int action) {
      Minecraft instance = Minecraft.getInstance();
      if (action == 1 && this instanceof IBreakableRestraint breakable && breakable.canBeBrokenOutOf()) {
         breakable.attemptToBreak(player, keyCode, action, instance.options);
      }
   }

   public void onMouseInput(Player player, int keyCode, int action) {
      Minecraft instance = Minecraft.getInstance();
      if (action == 1 && this instanceof IBreakableRestraint breakable && breakable.canBeBrokenOutOf()) {
         breakable.attemptToBreak(player, keyCode, action, instance.options);
      }
   }

   @Nonnull
   @OnlyIn(Dist.CLIENT)
   public abstract RestraintModelInterface getModelInterface();

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      tag.putString("Id", this.getId().toString());
      tag.putString("Type", this.getType().toString());
      if (this instanceof IEnchantableRestraint ench) {
         ItemEnchantments.CODEC
            .encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), ench.getEnchantments())
            .resultOrPartial(CuffedMod.LOGGER::error)
            .ifPresent(t -> tag.put("Enchantments", t));
      }

      tag.putUUID("Captor", this.captor);
      tag.put("ItemData", this.itemData);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      if (this instanceof IEnchantableRestraint ench) {
         if (nbt.contains("Enchantments")) {
            ItemEnchantments.CODEC
               .parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt.get("Enchantments"))
               .resultOrPartial(CuffedMod.LOGGER::error)
               .ifPresent(ench::setEnchantments);
         } else {
            ench.setEnchantments(ItemEnchantments.EMPTY);
         }
      }

      this.captor = nbt.getUUID("Captor");
      this.itemData = nbt.getCompound("ItemData");
   }

   public ItemStack saveToItemStack(Provider provider) {
      ItemStack stack = ItemStack.parse(provider, this.itemData).orElse(ItemStack.EMPTY);
      stack.setCount(1);
      if (this instanceof IBreakableRestraint breakable) {
         stack.setDamageValue(breakable.getMaxDurability() - breakable.getDurability());
      }

      if (this instanceof IEnchantableRestraint enchantable) {
         stack.set(DataComponents.ENCHANTMENTS, enchantable.getEnchantments());
      }

      return stack;
   }

   public void receiveUtilityPacketServer(ServerPlayer player, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
      if (utiltiyCode == 102 && this instanceof IBreakableRestraint breakable) {
         breakable.incrementDurability(player, integerArg);
      }
   }

   public void receiveUtilityPacketClient(Player player, int utiltiyCode, int integerArg, boolean booleanArg, double doubleArg, String stringArg) {
      if (utiltiyCode == 101) {
         this.clientSidedDurability = integerArg;
      } else if (utiltiyCode == 103 && this instanceof IBreakableRestraint breakable) {
         breakable.onBrokenClient(player);
      }
   }

   private int getEnchantLevelSafely(IEnchantableRestraint e, ResourceKey<Enchantment> key, RegistryAccess registryAccess) {
      return registryAccess.registryOrThrow(Registries.ENCHANTMENT).getHolder(key).map(e::getEnchantmentLevel).orElse(0);
   }
}
