package dev.alef.coloroutofspace.block;


import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class InfectedLeavesBlock extends LeavesBlock {

   public InfectedLeavesBlock(AbstractBlock.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, Integer.valueOf(7)).with(PERSISTENT, Boolean.valueOf(true)));
   }

   public VoxelShape func_230335_e_(BlockState p_230335_1_, IBlockReader p_230335_2_, BlockPos p_230335_3_) {
      return VoxelShapes.empty();
   }

   public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return 1;
   }
}