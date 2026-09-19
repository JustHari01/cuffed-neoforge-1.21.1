package com.lazrproductions.cuffed.event;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.PilloryBlock;
import com.lazrproductions.cuffed.cap.RestrainableCapability;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.ChainKnotEntity;
import com.lazrproductions.cuffed.entity.CrumblingBlockEntity;
import com.lazrproductions.cuffed.entity.base.IAnchorableEntity;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.entity.base.INicknamable;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.lazrproductions.cuffed.init.ModEnchantments;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModTags;
import com.lazrproductions.cuffed.items.PossessionsBox;
import com.lazrproductions.cuffed.restraints.base.AbstractArmRestraint;
import com.lazrproductions.cuffed.restraints.base.AbstractLegRestraint;
import com.lazrproductions.cuffed.restraints.base.IEnchantableRestraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.Tags.Blocks;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;

public class ModServerEvents {
   HashMap<UUID, CompoundTag> deadEntityRestraintData = new HashMap<>();
   HashMap<UUID, String> deadEntityNicknameData = new HashMap<>();

   @SubscribeEvent
   public void onPlayerCloned(Clone event) {
      if (event.isWasDeath()) {
         if (this.deadEntityRestraintData.containsKey(event.getEntity().getUUID())) {
            IRestrainableCapability n = CuffedAPI.Capabilities.getRestrainableCapability(event.getEntity());
            n.copyFrom(this.deadEntityRestraintData.get(event.getEntity().getUUID()), (ServerLevel)event.getEntity().level());
            this.deadEntityRestraintData.remove(event.getEntity().getUUID());
         }

         if (this.deadEntityNicknameData.containsKey(event.getEntity().getUUID())) {
            INicknamable nick = (INicknamable)event.getEntity();
            nick.deserializeNickname(this.deadEntityNicknameData.get(event.getEntity().getUUID()));
            this.deadEntityNicknameData.remove(event.getEntity().getUUID());
         }
      }
   }

   private static boolean canBreakReinforced(Player player) {
      return player != null && (player.isCreative() || player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.PICKAXES));
   }

   @SubscribeEvent
   public void playerMineBlock(BreakEvent event) {
      BlockState pickresult = event.getState();
      if (pickresult.is(ModTags.Blocks.REINFORCED_BLOCKS) && !canBreakReinforced(event.getPlayer())) {
         event.setCanceled(true);
      } else {
         Level level = (Level)event.getLevel();
         BlockPos pickpos = event.getPos();
         IDetainableEntity detainableEntity = (IDetainableEntity)event.getPlayer();
         if (detainableEntity.getDetained() > -1) {
            event.setCanceled(true);
         } else {
            if (CuffedAPI.Lockpicking.isLockedAt(level, pickresult, pickpos)) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public void onBreakSpeed(BreakSpeed event) {
      if (event.getState().is(ModTags.Blocks.REINFORCED_BLOCKS) && !canBreakReinforced(event.getEntity())) {
         event.setNewSpeed(-1.0F);
      }
   }

   @SubscribeEvent
   public void onExplosionDetonate(Detonate event) {
      event.getAffectedBlocks().removeIf(pos -> event.getLevel().getBlockState(pos).is(ModTags.Blocks.REINFORCED_BLOCKS));
   }

   @SubscribeEvent
   public void playerInteractBlock(RightClickBlock event) {
      if (event.getHand() == InteractionHand.MAIN_HAND) {
         Level level = event.getEntity().level();
         if (!level.isClientSide()) {
            Player interacting = event.getEntity();
            IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(interacting);
            IDetainableEntity interactingAsDetainable = (IDetainableEntity)interacting;
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);
            if (interactingAsDetainable.getDetained() > -1) {
               event.setCancellationResult(InteractionResult.FAIL);
               event.setCanceled(true);
               return;
            }

            ArrayList<IAnchorableEntity> entitiesAnchoredToInteractor = new ArrayList<>(0);
            ServerLevel server = (ServerLevel)event.getLevel();
            if (server != null) {
               for (Object var12 : server.getAllEntities()) {
                  if (var12 instanceof IAnchorableEntity) {
                     IAnchorableEntity en = (IAnchorableEntity)var12;
                     if (en.isAnchored() && en.getAnchor().getUUID() == interacting.getUUID()) {
                        entitiesAnchoredToInteractor.add(en);
                     }
                  }
               }
            }

            if ((
                  state.is(Blocks.FENCES) && (Boolean)CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_FENCES.get()
                     || state.is(net.minecraft.world.level.block.Blocks.TRIPWIRE_HOOK)
                        && (Boolean)CuffedMod.SERVER_CONFIG.ANCHORING_ALLOW_ANCHORING_TO_TRIPWIRE_HOOKS.get()
               )
               && entitiesAnchoredToInteractor.size() > 0) {
               for (int i = 0; i < entitiesAnchoredToInteractor.size(); i++) {
                  ChainKnotEntity.bindEntityToNewOrExistingKnot((LivingEntity)entitiesAnchoredToInteractor.get(i), level, event.getPos());
               }

               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
               return;
            }

            if (state.is((Block)ModBlocks.PILLORY.get())) {
               ServerPlayer whoIWasEscorting = cap.getWhoImEscorting();
               if (whoIWasEscorting != null) {
                  if (level.getBlockState(pos.above()).is((Block)ModBlocks.PILLORY.get())) {
                     pos = pos.above();
                     state = level.getBlockState(pos);
                  }

                  cap.stopEscortingPlayer();
                  Vec3 p = PilloryBlock.getPositionBehind(state, pos);
                  whoIWasEscorting.teleportTo(p.x(), p.y(), p.z());
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  event.setCanceled(true);
                  return;
               }
            }

            if (state.is((Block)ModBlocks.BUNK.get()) && cap.getWhoImEscorting() != null) {
               state.useWithoutItem(event.getLevel(), cap.getWhoImEscorting(), event.getHitVec());
               event.setCancellationResult(InteractionResult.SUCCESS);
               event.setCanceled(true);
               return;
            }

            if (state.is(ModTags.Blocks.REINFORCED_BLOCKS) && Block.isShapeFullBlock(state.getShape(level, pos))) {
               ItemStack stack = event.getItemStack();
               if (stack.is((Item)ModItems.FORK.get()) || stack.is((Item)ModItems.SPOON.get())) {
                  Random r = new Random();
                  if (r.nextFloat() < 0.25F) {
                     CrumblingBlockEntity.crumbleBlock(level, pos, state, 1);
                  }

                  level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 1.0F, r.nextFloat() * 0.2F + 0.9F);
                  level.levelEvent(null, 2001, pos, Block.getId(state));
                  stack.hurtAndBreak(1, interacting, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  event.setCanceled(true);
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void playerInteractEntity(EntityInteract event) {
      if (event.getSide() != LogicalSide.CLIENT) {
         if (event.getHand() == InteractionHand.MAIN_HAND && event.getSide() == LogicalSide.SERVER) {
            ServerPlayer player = (ServerPlayer)event.getEntity();
            IRestrainableCapability myCap = CuffedAPI.Capabilities.getRestrainableCapability(player);
            IDetainableEntity detainableEntity = (IDetainableEntity)player;
            if (detainableEntity.getDetained() > -1) {
               event.setCancellationResult(InteractionResult.FAIL);
               event.setCanceled(true);
               return;
            }

            if (event.getTarget() instanceof ServerPlayer target) {
               IRestrainableCapability targetCap = CuffedAPI.Capabilities.getRestrainableCapability(target);
               double maxDist = player.getEyePosition().distanceTo(target.position());
               Vec3 interactionPos = new Vec3(
                  target.position().x, player.getLookAngle().multiply(new Vec3(maxDist, maxDist, maxDist)).add(player.getEyePosition()).y, target.position().z
               );
               if (event.getItemStack().is((Item)ModItems.POSSESSIONSBOX.get()) && targetCap.armsRestrained()) {
                  PossessionsBox.frisk(player, target, event.getItemStack());
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  event.setCanceled(true);
                  return;
               }

               if (targetCap != null) {
                  targetCap.onInteractedByOther(target, player, interactionPos.y - target.position().y, event.getItemStack(), event.getHand(), false);
               }
            }

            if (event.getTarget().getType().is(ModTags.Entities.CHAINABLE_ENTITIES)) {
               IAnchorableEntity anchorableEntity = (IAnchorableEntity)event.getTarget();
               if ((Boolean)CuffedMod.SERVER_CONFIG.ANCHORING_ANCHOR_ONLY_WHEN_RESTRAINED.get() && event.getTarget() instanceof Player p) {
                  IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
                  if (!cap.isRestrained()) {
                     return;
                  }
               }

               if (anchorableEntity.isAnchored()) {
                  if (player.getItemInHand(event.getHand()).is(Items.AIR)) {
                     anchorableEntity.setAnchoredTo(null);
                     player.level().playSound(null, event.getPos(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
                     event.setCancellationResult(InteractionResult.SUCCESS);
                     event.setCanceled(true);
                     return;
                  }
               } else if (player.getItemInHand(event.getHand()).is(Items.CHAIN)) {
                  anchorableEntity.setAnchoredTo(player);
                  player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                  player.level().playSound(null, event.getPos(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.7F, 1.0F);
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  event.setCanceled(true);
                  return;
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      ServerPlayer p = (ServerPlayer)event.getEntity();
      if (p != null) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
         cap.onLoginServer(p);
         if (cap.isRestrained()) {
            CuffedAPI.Networking.sendRestraintSyncPacket(p);
         }
      }
   }

   @SubscribeEvent
   public void onPlayerLogout(PlayerLoggedOutEvent event) {
      ServerPlayer p = (ServerPlayer)event.getEntity();
      if (p != null) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(p);
         cap.onLogoutServer(p);
      }
   }

   @SubscribeEvent
   public void onEntityDied(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null) {
            cap.onDeathServer(player);
            this.deadEntityRestraintData.put(event.getEntity().getUUID(), cap.serializeNBT(player.level().registryAccess()));
         }

         INicknamable nick = (INicknamable)player;
         if ((Boolean)CuffedMod.SERVER_CONFIG.NICKNAME_PERSISTS_ON_DEATH.get() && nick.getNickname() != null) {
            this.deadEntityNicknameData.put(player.getUUID(), nick.serializeNickname());
         }
      }

      if (event.getEntity() instanceof IAnchorableEntity anchorable && anchorable.isAnchored()) {
         anchorable.setAnchoredTo(null);
      }
   }

   @SubscribeEvent
   public void onLand(LivingFallEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null) {
            event.setDamageMultiplier(cap.onLandServer(player, event.getDistance(), event.getDamageMultiplier()));
         }
      }
   }

   @SubscribeEvent
   public void onJump(LivingJumpEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null) {
            cap.onJumpServer(player);
         }
      }
   }

   @SubscribeEvent
   public void onLivingDamaged(Pre event) {
      if (event.getEntity() instanceof Player captor && !event.getEntity().level().isClientSide()) {
         float originalAmount = event.getNewDamage();
         ServerLevel level = (ServerLevel)event.getEntity().level();
         MinecraftServer server = event.getEntity().level().getServer();
         if (server != null) {
            boolean activateImbue = true;
            ArrayList<Player> playersToTakeDamage = new ArrayList<>();
            List<ServerPlayer> players = server.getPlayerList().getPlayers();

            for (int i = 0; i < players.size(); i++) {
               IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability((Player)players.get(i));
               AbstractArmRestraint arm = cap.getArmRestraint();
               if (arm != null && arm.getCaptor(level) == captor) {
                  playersToTakeDamage.add((Player)players.get(i));
               }

               AbstractLegRestraint leg = cap.getLegRestraint();
               if (leg != null && leg.getCaptor(level) == captor) {
                  playersToTakeDamage.add((Player)players.get(i));
               }
            }

            if (activateImbue) {
               float amountNegated = 0.0F;
               Optional<Reference<Enchantment>> imbueHolderOpt = level.registryAccess()
                  .registryOrThrow(Registries.ENCHANTMENT)
                  .getHolder(ModEnchantments.IMBUE);
               if (imbueHolderOpt.isPresent()) {
                  Holder<Enchantment> imbueHolder = (Holder<Enchantment>)imbueHolderOpt.get();

                  for (Player pl : playersToTakeDamage) {
                     IRestrainableCapability capx = CuffedAPI.Capabilities.getRestrainableCapability(pl);
                     if (capx != null && capx.armsRestrained() && capx.getArmRestraint() instanceof IEnchantableRestraint e && e.hasEnchantment(imbueHolder)) {
                        int enchLevel = e.getEnchantmentLevel(imbueHolder);
                        float percentage = (float)enchLevel / 3.0F * 0.8F;
                        amountNegated += originalAmount * percentage;
                     }

                     if (capx != null && capx.legsRestrained() && capx.getLegRestraint() instanceof IEnchantableRestraint e && e.hasEnchantment(imbueHolder)) {
                        int enchLevel = e.getEnchantmentLevel(imbueHolder);
                        float percentage = (float)enchLevel / 3.0F * 0.8F;
                        amountNegated += originalAmount * percentage;
                     }

                     pl.hurt(captor.damageSources().magic(), amountNegated / (float)playersToTakeDamage.size());
                  }

                  event.setNewDamage(Mth.clamp(originalAmount - amountNegated, 0.0F, originalAmount));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onCommand(CommandEvent event) {
      ServerPlayer player = ((CommandSourceStack)event.getParseResults().getContext().getSource()).getPlayer();
      if (player != null) {
         RestrainableCapability cap = (RestrainableCapability)CuffedAPI.Capabilities.getRestrainableCapability(player);
         if (cap != null && cap.restraintsDisabledMovement() && !player.hasPermissions(2)) {
            player.sendSystemMessage(Component.literal("You cannot do this right now.").withStyle(ChatFormatting.RED));
            event.setCanceled(true);
         }
      }
   }
}
