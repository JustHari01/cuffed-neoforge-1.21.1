package com.lazrproductions.cuffed.blocks;

import com.lazrproductions.cuffed.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ReinforcedBarsBlock extends IronBarsBlock {
   public static final IntegerProperty COLUMN = IntegerProperty.create("column", 0, 2);

   public ReinforcedBarsBlock(Properties p) {
      super(p);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false))
                        .setValue(EAST, false))
                     .setValue(SOUTH, false))
                  .setValue(WEST, false))
               .setValue(WATERLOGGED, false))
            .setValue(COLUMN, 0)
      );
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED, COLUMN});
   }

   public BlockState getStateForPlacement(@Nonnull BlockPlaceContext ctx) {
      Level level = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      boolean up = level.getBlockState(pos.above()).is((Block)ModBlocks.REINFORCED_BARS.get())
         || level.getBlockState(pos.above()).is((Block)ModBlocks.CELL_DOOR.get())
         || level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS_GAPPED.get());
      boolean down = level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS.get())
         || level.getBlockState(pos.below()).is((Block)ModBlocks.CELL_DOOR.get())
         || level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS_GAPPED.get());
      int column = 0;
      if (up && down) {
         column = 1;
      }

      if (down && !up) {
         column = 2;
      }

      return (BlockState)super.getStateForPlacement(ctx).setValue(COLUMN, column);
   }

   public BlockState updateShape(
      @Nonnull BlockState state,
      @Nonnull Direction updateDirection,
      @Nonnull BlockState otherState,
      @Nonnull LevelAccessor level,
      @Nonnull BlockPos pos,
      @Nonnull BlockPos otherPos
   ) {
      if (updateDirection != Direction.DOWN && updateDirection != Direction.UP) {
         return super.updateShape(state, updateDirection, otherState, level, pos, otherPos);
      } else {
         boolean up = level.getBlockState(pos.above()).is((Block)ModBlocks.REINFORCED_BARS.get())
            || level.getBlockState(pos.above()).is((Block)ModBlocks.CELL_DOOR.get())
            || level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS_GAPPED.get());
         boolean down = level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS.get())
            || level.getBlockState(pos.below()).is((Block)ModBlocks.CELL_DOOR.get())
            || level.getBlockState(pos.below()).is((Block)ModBlocks.REINFORCED_BARS_GAPPED.get());
         int column = 0;
         if (up && down) {
            column = 1;
         }

         if (down && !up) {
            column = 2;
         }

         return super.updateShape((BlockState)state.setValue(COLUMN, column), updateDirection, otherState, level, pos, otherPos);
      }
   }

   public int getColumn(BlockState state) {
      return (Integer)state.getValue(COLUMN);
   }
}
