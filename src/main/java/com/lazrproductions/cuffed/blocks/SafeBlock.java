package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.entity.SafeBlockEntity;
import com.lazrproductions.cuffed.init.ModItems;
import com.lazrproductions.cuffed.init.ModStatistics;
import com.lazrproductions.cuffed.items.KeyItem;
import com.lazrproductions.cuffed.items.KeyRingItem;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SafeBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<SafeBlock> CODEC = simpleCodec(SafeBlock::new);
   public static final VoxelShape SHAPE_EW = Block.box(1.0, 0.0, 2.0, 15.0, 16.0, 14.0);
   public static final VoxelShape SHAPE_NS = Block.box(2.0, 0.0, 1.0, 14.0, 16.0, 15.0);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public SafeBlock(Properties p_49046_) {
      super(p_49046_);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, Boolean.FALSE))
            .setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return state.getValue(FACING) != Direction.EAST && state.getValue(FACING) != Direction.WEST ? SHAPE_NS : SHAPE_EW;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      InteractionResult res = this.interact(stack, state, level, pos, player, hand, hitResult);
      return res.consumesAction() ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return this.interact(ItemStack.EMPTY, state, level, pos, player, InteractionHand.MAIN_HAND, hitResult);
   }

   private InteractionResult interact(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof SafeBlockEntity safe) {
            boolean isKeyItemAndIsBoundToThis = stack.is((Item)ModItems.KEY.get()) && KeyItem.isBoundToLock(stack, safe.getLockId());
            boolean isKeyRingItemAndIsBoundToThis = stack.is((Item)ModItems.KEY_RING.get()) && KeyRingItem.hasBoundId(stack, safe.getLockId());
            boolean isCreativeKey = stack.is((Item)ModItems.CREATIVE_KEY.get());
            boolean isCreativeBindBreaker = stack.is((Item)ModItems.CREATIVE_BIND_BREAKER.get());
            boolean isHoldingLockpick = stack.is((Item)ModItems.LOCKPICK.get());
            if (!isHoldingLockpick) {
               if (isCreativeBindBreaker) {
                  safe.bind();
                  player.sendSystemMessage(
                     Component.translatable("item.cuffed.creative_bind_breaker.use", new Object[]{Component.translatable(this.getDescriptionId())})
                  );
                  return InteractionResult.SUCCESS;
               }

               if (isKeyItemAndIsBoundToThis || isKeyRingItemAndIsBoundToThis || isCreativeKey) {
                  boolean willEndUpLocked = !safe.isLocked();
                  safe.setLocked(willEndUpLocked, level, player, pos);
                  return InteractionResult.sidedSuccess(level.isClientSide());
               }
            } else if (safe.isLocked()) {
               CuffedAPI.Networking.sendLockpickBeginPickingCellDoorPacketToClient(
                  (ServerPlayer)player,
                  pos,
                  (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_SPEED_INCREASE_PER_PICK_FOR_BREAKING_SAFES.get(),
                  (Integer)CuffedMod.SERVER_CONFIG.LOCKPICKING_PROGRESS_PER_PICK_FOR_BREAKING_SAFES.get()
               );
               return InteractionResult.sidedSuccess(level.isClientSide());
            }

            if (!safe.isLocked()) {
               player.openMenu((SafeBlockEntity)blockentity);
               player.awardStat((ResourceLocation)ModStatistics.OPEN_SAFE.get());
               PiglinAi.angerNearbyPiglins(player, true);
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, OPEN, WATERLOGGED});
   }

   public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean p_49080_) {
      if (!state.is(newState.getBlock())) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof Container) {
            Containers.dropContents(level, pos, (Container)blockentity);
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, p_49080_);
      }
   }

   public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
      BlockEntity blockentity = level.getBlockEntity(pos);
      if (blockentity instanceof SafeBlockEntity) {
         ((SafeBlockEntity)blockentity).recheckOpen();
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return new SafeBlockEntity(pos, state);
   }

   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.MODEL;
   }

   public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack fromStack) {
      if (fromStack.has(DataComponents.CUSTOM_NAME)) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof SafeBlockEntity) {
            ((SafeBlockEntity)blockentity).setCustomName(fromStack.getHoverName());
         }
      }
   }

   public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
      return true;
   }

   public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull PathComputationType path) {
      return false;
   }

   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
   }

   public FluidState getFluidState(@Nonnull BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Deprecated
   public BlockState updateShape(
      @Nonnull BlockState blockState,
      @Nonnull Direction direction,
      @Nonnull BlockState facingState,
      @Nonnull LevelAccessor levelAccessor,
      @Nonnull BlockPos currentPos,
      @Nonnull BlockPos facingPos
   ) {
      if ((Boolean)blockState.getValue(WATERLOGGED)) {
         levelAccessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(blockState, direction, facingState, levelAccessor, currentPos, facingPos);
   }
}
