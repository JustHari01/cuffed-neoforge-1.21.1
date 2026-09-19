package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.blocks.entity.TrayBlockEntity;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class TrayBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<TrayBlock> CODEC = simpleCodec(TrayBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE_NS = Block.box(2.0, 0.0, 4.0, 14.0, 1.0, 12.0);
   protected static final VoxelShape SHAPE_EW = Block.box(4.0, 0.0, 2.0, 12.0, 1.0, 14.0);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public TrayBlock(Properties p) {
      super(p);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      return state.getValue(FACING) != Direction.EAST && state.getValue(FACING) != Direction.WEST ? SHAPE_NS : SHAPE_EW;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      InteractionResult res = this.interact(state, level, pos, player, hand, hitResult);
      return res.consumesAction() ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      return this.interact(state, level, pos, player, InteractionHand.MAIN_HAND, hitResult);
   }

   private InteractionResult interact(BlockState state, Level level, BlockPos pos, Player interacting, InteractionHand hand, BlockHitResult hitResult) {
      return level.getBlockEntity(pos) instanceof TrayBlockEntity entity ? entity.use(state, level, pos, interacting, hand, hitResult) : InteractionResult.FAIL;
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return new TrayBlockEntity(pos, state);
   }

   public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean flag) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TrayBlockEntity tray) {
         tray.dropItem(level, pos);
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, flag);
   }

   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.MODEL;
   }

   public boolean isPathfindable(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull PathComputationType path) {
      return false;
   }

   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
   }

   public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
      if (level.getBlockEntity(pos) instanceof TrayBlockEntity e) {
         e.loadFromItem(stack, level, pos, state);
      }

      super.setPlacedBy(level, pos, state, entity, stack);
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
