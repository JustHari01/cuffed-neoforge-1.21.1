package com.lazrproductions.cuffed.blocks;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedBarsGappedBlock extends Block implements SimpleWaterloggedBlock {
   public static final VoxelShape SHAPE_EW = Shapes.or(
      Block.box(7.0, 0.0, 0.0, 9.0, 3.0, 16.0),
      new VoxelShape[]{Block.box(7.0, 3.0, 0.0, 9.0, 13.0, 1.0), Block.box(7.0, 3.0, 15.0, 9.0, 13.0, 16.0), Block.box(7.0, 13.0, 0.0, 9.0, 16.0, 16.0)}
   );
   public static final VoxelShape SHAPE_NS = Shapes.or(
      Block.box(0.0, 0.0, 7.0, 16.0, 3.0, 9.0),
      new VoxelShape[]{Block.box(0.0, 3.0, 7.0, 1.0, 13.0, 9.0), Block.box(15.0, 3.0, 7.0, 16.0, 13.0, 9.0), Block.box(0.0, 13.0, 7.0, 16.0, 16.0, 9.0)}
   );
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   public ReinforcedBarsGappedBlock(Properties ctx) {
      super(ctx);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return state.getValue(FACING) != Direction.EAST && state.getValue(FACING) != Direction.WEST ? SHAPE_NS : SHAPE_EW;
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()))
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
