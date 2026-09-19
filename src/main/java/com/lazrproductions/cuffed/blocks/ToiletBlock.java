package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.blocks.entity.ToiletBlockEntity;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ToiletBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<ToiletBlock> CODEC = simpleCodec(ToiletBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE_NORTH = Shapes.or(
      Shapes.box(0.0, 0.0, 0.75, 1.0, 1.125, 1.0),
      new VoxelShape[]{Shapes.box(0.3125, 0.0, 0.3125, 0.6875, 0.25, 0.75), Shapes.box(0.125, 0.25, 0.125, 0.875, 0.4375, 0.75)}
   );
   protected static final VoxelShape SHAPE_EAST = Shapes.or(
      Shapes.box(0.0, 0.0, 0.0, 0.25, 1.125, 1.0),
      new VoxelShape[]{Shapes.box(0.25, 0.0, 0.3125, 0.6875, 0.25, 0.6875), Shapes.box(0.25, 0.25, 0.125, 0.875, 0.4375, 0.875)}
   );
   protected static final VoxelShape SHAPE_SOUTH = Shapes.or(
      Shapes.box(0.0, 0.0, 0.0, 1.0, 1.125, 0.25),
      new VoxelShape[]{Shapes.box(0.3125, 0.0, 0.25, 0.6875, 0.25, 0.6875), Shapes.box(0.125, 0.25, 0.25, 0.875, 0.4375, 0.875)}
   );
   protected static final VoxelShape SHAPE_WEST = Shapes.or(
      Shapes.box(0.75, 0.0, 0.0, 1.0, 1.125, 1.0),
      new VoxelShape[]{Shapes.box(0.3125, 0.0, 0.3125, 0.75, 0.25, 0.6875), Shapes.box(0.125, 0.25, 0.125, 0.75, 0.4375, 0.875)}
   );

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public ToiletBlock(Properties p) {
      super(p);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED});
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      switch ((Direction)state.getValue(FACING)) {
         case NORTH:
            return SHAPE_NORTH;
         case EAST:
            return SHAPE_EAST;
         case SOUTH:
            return SHAPE_SOUTH;
         default:
            return SHAPE_WEST;
      }
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
      return !interacting.isCrouching() && level.getBlockEntity(pos) instanceof ToiletBlockEntity entity
         ? entity.use(state, level, pos, interacting, hand, hitResult)
         : InteractionResult.FAIL;
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return new ToiletBlockEntity(pos, state);
   }

   public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean f) {
      if (level.getBlockEntity(pos) instanceof ToiletBlockEntity toilet) {
         toilet.getDrops(level, pos)
            .forEach(
               c -> {
                  double d0 = (double)EntityType.ITEM.getWidth();
                  double d1 = 0.2;
                  double d2 = d0 / 2.0;
                  double d3 = Math.floor((double)pos.getX()) + level.random.nextDouble() * d1 + d2;
                  double d4 = Math.floor((double)pos.getY()) + level.random.nextDouble() * d1;
                  double d5 = Math.floor((double)pos.getZ()) + level.random.nextDouble() * d1 + d2;
                  ItemEntity e = new ItemEntity(level, d3, d4, d5, c);
                  e.setDeltaMovement(
                     level.random.triangle(0.0, 0.11485000171139836),
                     level.random.triangle(0.2, 0.11485000171139836),
                     level.random.triangle(0.0, 0.11485000171139836)
                  );
                  e.setDefaultPickUpDelay();
                  level.addFreshEntity(e);
               }
            );
      }

      super.onRemove(state, level, pos, newState, f);
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
