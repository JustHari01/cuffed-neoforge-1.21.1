package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.api.CuffedAPI;
import com.lazrproductions.cuffed.blocks.base.DetentionBlock;
import com.lazrproductions.cuffed.cap.base.IRestrainableCapability;
import com.lazrproductions.cuffed.entity.base.IDetainableEntity;
import com.lazrproductions.cuffed.init.ModSounds;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class PilloryBlock extends DetentionBlock {
   public static final MapCodec<PilloryBlock> CODEC = simpleCodec(PilloryBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   protected static final BooleanProperty CLOSED = BooleanProperty.create("closed");
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   protected static final VoxelShape SHAPE_NS_CLOSED = Shapes.or(
      Block.box(-1.0, 0.0, 6.0, 2.0, 9.0, 10.0), new VoxelShape[]{Block.box(14.0, 0.0, 6.0, 17.0, 9.0, 10.0), Block.box(2.0, 0.0, 7.0, 14.0, 4.0, 9.0)}
   );
   protected static final VoxelShape SHAPE_NS_OPEN = Shapes.or(
      Block.box(-1.0, 0.0, 6.0, 2.0, 9.0, 10.0), new VoxelShape[]{Block.box(14.0, 0.0, 6.0, 17.0, 9.0, 10.0), Block.box(2.0, 3.0, 7.0, 14.0, 8.0, 9.0)}
   );
   protected static final VoxelShape SHAPE_NS_BASE = Shapes.or(
      Block.box(-1.0, 0.0, 6.0, 2.0, 16.0, 10.0), new VoxelShape[]{Block.box(14.0, 0.0, 6.0, 17.0, 16.0, 10.0), Block.box(2.0, 10.0, 7.0, 14.0, 15.0, 9.0)}
   );
   protected static final VoxelShape SHAPE_EW_CLOSED = Shapes.or(
      Block.box(6.0, 0.0, 14.0, 10.0, 9.0, 17.0), new VoxelShape[]{Block.box(7.0, 0.0, 2.0, 9.0, 4.0, 14.0), Block.box(6.0, 0.0, -1.0, 10.0, 9.0, 2.0)}
   );
   protected static final VoxelShape SHAPE_EW_OPEN = Shapes.or(
      Block.box(6.0, 0.0, 14.0, 10.0, 9.0, 17.0), new VoxelShape[]{Block.box(7.0, 3.0, 2.0, 9.0, 8.0, 14.0), Block.box(6.0, 0.0, -1.0, 10.0, 9.0, 2.0)}
   );
   protected static final VoxelShape SHAPE_EW_BASE = Shapes.or(
      Block.box(6.0, 0.0, 14.0, 10.0, 16.0, 17.0), new VoxelShape[]{Block.box(6.0, 0.0, -1.0, 10.0, 16.0, 2.0), Block.box(7.0, 10.0, 2.0, 9.0, 15.0, 14.0)}
   );

   protected MapCodec<? extends DetentionBlock> codec() {
      return CODEC;
   }

   public PilloryBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(CLOSED, false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   @Nullable
   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
      BlockPos blockpos = ctx.getClickedPos();
      Level level = ctx.getLevel();
      return blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(ctx)
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection())).setValue(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
         return state.getValue(FACING) != Direction.NORTH && state.getValue(FACING) != Direction.SOUTH ? SHAPE_EW_BASE : SHAPE_NS_BASE;
      } else {
         return state.getValue(FACING) != Direction.NORTH && state.getValue(FACING) != Direction.SOUTH
            ? (state.getValue(CLOSED) ? SHAPE_EW_CLOSED : SHAPE_EW_OPEN)
            : (state.getValue(CLOSED) ? SHAPE_NS_CLOSED : SHAPE_NS_OPEN);
      }
   }

   public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext ctx) {
      return this.getShape(state, getter, pos, ctx);
   }

   public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation r) {
      return (BlockState)state.setValue(FACING, r.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror m) {
      return state.rotate(m.getRotation((Direction)state.getValue(FACING)));
   }

   public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
      level.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public BlockState updateShape(
      @Nonnull BlockState state,
      @Nonnull Direction direction,
      @Nonnull BlockState otherState,
      @Nonnull LevelAccessor level,
      @Nonnull BlockPos pos,
      @Nonnull BlockPos otherPos
   ) {
      DoubleBlockHalf thisHalf = (DoubleBlockHalf)state.getValue(HALF);
      if (direction.getAxis() == Axis.Y && thisHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
         return otherState.is(this) && otherState.getValue(HALF) != thisHalf
            ? (BlockState)((BlockState)state.setValue(FACING, (Direction)otherState.getValue(FACING))).setValue(CLOSED, (Boolean)otherState.getValue(CLOSED))
            : Blocks.AIR.defaultBlockState();
      } else {
         return thisHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
      }
   }

   public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, @Nonnull BlockPos pos) {
      BlockPos blockpos = pos.below();
      BlockState blockstate = level.getBlockState(blockpos);
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockstate.isFaceSturdy(level, blockpos, Direction.UP) : blockstate.is(this);
   }

   public BlockState playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!level.isClientSide && player.isCreative()) {
         preventCreativeDropFromBottomPart(level, pos, state, player);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING, HALF, CLOSED});
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

   public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      IDetainableEntity en = (IDetainableEntity)player;
      if (en.getDetained() > -1) {
         return InteractionResult.FAIL;
      } else if (!player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
         if (!level.isClientSide) {
            BlockState state1 = level.getBlockState(pos.above());
            ((PilloryBlock)state.getBlock()).interact(state1, level, pos.above(), player, hand, hit);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else if (!player.isCrouching() && !level.isClientSide) {
         boolean wasOpen = !this.getClosed(state);
         boolean shouldBeOpen = this.attemptToToggleDetained(level, wasOpen, state, pos);
         if (shouldBeOpen != wasOpen) {
            Random r = new Random();
            level.playSound(null, pos, ModSounds.PILLORY_USE, SoundSource.BLOCKS, 1.0F, (state.getValue(CLOSED) ? 1.0F : 0.8F) + r.nextFloat() * 0.1F);
            level.setBlock(pos, (BlockState)state.setValue(CLOSED, !shouldBeOpen), 11);
         }

         return InteractionResult.SUCCESS;
      } else {
         return !player.isCrouching() && state.getValue(HALF) == DoubleBlockHalf.UPPER && level.isClientSide
            ? InteractionResult.SUCCESS
            : InteractionResult.PASS;
      }
   }

   public void setClosed(Level level, BlockPos pos, BlockState state, boolean v) {
      level.setBlock(pos, (BlockState)state.setValue(CLOSED, v), 1);
   }

   public boolean getClosed(BlockState state) {
      return (Boolean)state.getValue(CLOSED);
   }

   protected static void preventCreativeDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
      DoubleBlockHalf doubleblockhalf = (DoubleBlockHalf)state.getValue(HALF);
      if (doubleblockhalf == DoubleBlockHalf.UPPER) {
         BlockPos blockpos = pos.below();
         BlockState blockstate = level.getBlockState(blockpos);
         if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            level.setBlock(blockpos, blockstate1, 35);
            level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
         }
      }
   }

   public float getFacingRotation(BlockState state, BlockPos pos) {
      return ((Direction)state.getValue(FACING)).toYRot();
   }

   public static Vec3 getPositionBehind(BlockState state, BlockPos pos) {
      double xOffset = state.getValue(FACING) == Direction.EAST ? -0.363 : (state.getValue(FACING) == Direction.WEST ? 0.363 : 0.0);
      double zOffset = state.getValue(FACING) == Direction.SOUTH ? -0.363 : (state.getValue(FACING) == Direction.NORTH ? 0.363 : 0.0);
      return pos.getCenter().add(new Vec3(xOffset, -1.5, zOffset));
   }

   public boolean attemptToToggleDetained(@Nonnull Level level, boolean wasOpen, BlockState state, BlockPos pos) {
      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         Vec3 behind = getPositionBehind(state, pos);
         Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
         if (p != null) {
            double dist = Math.sqrt(Math.pow(behind.z - p.position().z, 2.0) + Math.pow(behind.x - p.position().x, 2.0));
            if (dist < 0.3F) {
               IDetainableEntity detainableEntity = (IDetainableEntity)p;
               if (wasOpen) {
                  if (detainableEntity.getDetained() == -1 && this.canDetainPlayer(level, state, pos, p, true)) {
                     detainableEntity.detainToBlock(
                        level, new Vector3f((float)behind.x(), (float)behind.y(), (float)behind.z()), pos, 0, this.getFacingRotation(state, pos)
                     );
                     return false;
                  }

                  return true;
               }

               if (detainableEntity.getDetained() == 0) {
                  detainableEntity.undetain();
                  return true;
               }
            }
         }
      }

      return !wasOpen;
   }

   public static Player getDetainedEntity(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos) {
      Vec3 behind = getPositionBehind(state, pos);
      Player p = level.getNearestPlayer(TargetingConditions.forNonCombat(), behind.x, behind.y, behind.z);
      if (p != null) {
         IDetainableEntity detain = (IDetainableEntity)p;
         if (detain.getDetained() > -1) {
            double dist = Math.sqrt(Math.pow(behind.z - p.position().z, 2.0) + Math.pow(behind.x - p.position().x, 2.0));
            if (dist < 0.3F) {
               return p;
            }
         }
      }

      return null;
   }

   @Override
   public boolean canDetainPlayer(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull Player player, boolean ignoreState) {
      IRestrainableCapability cap = CuffedAPI.Capabilities.getRestrainableCapability(player);
      return cap.armsRestrained() ? false : this.getClosed(state) || ignoreState;
   }
}
