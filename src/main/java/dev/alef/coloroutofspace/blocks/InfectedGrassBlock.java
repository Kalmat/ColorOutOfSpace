package dev.alef.coloroutofspace.blocks;

import dev.alef.coloroutofspace.lists.BlockList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class InfectedGrassBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   public InfectedGrassBlock(AbstractBlock.Properties properties) {
      super(properties);
   }
   
   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	   return SHAPE;
   }
	   
   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
	   Block block = state.getBlock();
	   return block.equals(BlockList.color_dirt_block);
   }

   public AbstractBlock.OffsetType getOffsetType() {
      return AbstractBlock.OffsetType.XYZ;
   }
}