package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.blocks.entity.GuillotineBlockEntity;
import com.lazrproductions.cuffed.init.ModBlockEntities;
import com.lazrproductions.cuffed.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GuillotineBlock extends BaseEntityBlock {
   public static final MapCodec<GuillotineBlock> CODEC = simpleCodec(GuillotineBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   protected static final VoxelShape SHAPE_NS_BASE = Shapes.or(
      Block.box(-1.0, 0.0, 6.0, 2.0, 16.0, 10.0), new VoxelShape[]{Block.box(14.0, 0.0, 6.0, 17.0, 16.0, 10.0), Block.box(2.0, 10.0, 7.0, 14.0, 15.0, 9.0)}
   );
   protected static final VoxelShape SHAPE_EW_BASE = Shapes.or(
      Block.box(6.0, 0.0, 14.0, 10.0, 16.0, 17.0), new VoxelShape[]{Block.box(6.0, 0.0, -1.0, 10.0, 16.0, 2.0), Block.box(7.0, 10.0, 2.0, 9.0, 15.0, 14.0)}
   );

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public GuillotineBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      return state.getValue(FACING) != Direction.NORTH && state.getValue(FACING) != Direction.SOUTH ? SHAPE_EW_BASE : SHAPE_NS_BASE;
   }

   public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      return this.getShape(state, getter, pos, ctx);
   }

   @Nullable
   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
      BlockPos blockpos = ctx.getClickedPos();
      Level level = ctx.getLevel();
      BlockState stateBelow = level.getBlockState(blockpos.below());
      return stateBelow.is((Block)ModBlocks.PILLORY.get())
         ? (BlockState)this.defaultBlockState().setValue(FACING, (Direction)stateBelow.getValue(PilloryBlock.FACING))
         : null;
   }

   public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation r) {
      return (BlockState)state.setValue(FACING, r.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror m) {
      return state.rotate(m.getRotation((Direction)state.getValue(FACING)));
   }

   public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
      BlockPos blockpos = pos.below();
      BlockState blockstate = level.getBlockState(blockpos);
      return blockstate.is((Block)ModBlocks.PILLORY.get());
   }

   public BlockState updateShape(
      @Nonnull BlockState state,
      @Nonnull Direction direction,
      @Nonnull BlockState otherState,
      @Nonnull LevelAccessor level,
      @Nonnull BlockPos pos,
      @Nonnull BlockPos otherPos
   ) {
      return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING});
   }

   public RenderShape getRenderShape(@Nonnull BlockState p_49232_) {
      return RenderShape.MODEL;
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

   private InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
      if (hand == InteractionHand.MAIN_HAND) {
         if (!level.isClientSide() && level.getBlockEntity(pos) instanceof GuillotineBlockEntity bl) {
            bl.interact(level, pos, state);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.sidedSuccess(level.isClientSide());
         }
      } else {
         return InteractionResult.FAIL;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return new GuillotineBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> blockEntityType) {
      if (level.isClientSide()) {
         return null;
      } else {
         return state.is((Block)ModBlocks.GUILLOTINE.get())
            ? createTickerHelper(blockEntityType, (BlockEntityType)ModBlockEntities.GUILLOTINE.get(), (l, pos, s, blockEntity) -> ((GuillotineBlockEntity)blockEntity).tick(l, pos, s))
            : null;
      }
   }
}
