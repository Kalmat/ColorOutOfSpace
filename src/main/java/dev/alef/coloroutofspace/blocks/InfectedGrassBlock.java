
package dev.alef.coloroutofspace.blocks;

import java.util.Random;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public class InfectedGrassBlock extends Block implements IGrowable {
   public InfectedGrassBlock(AbstractBlock.Properties p_i241184_1_) {
	   super(p_i241184_1_);
   }

   private static boolean func_235516_b_(BlockState p_235516_0_, IWorldReader p_235516_1_, BlockPos p_235516_2_) {
      BlockPos blockpos = p_235516_2_.up();
      BlockState blockstate = p_235516_1_.getBlockState(blockpos);
      int i = LightEngine.func_215613_a(p_235516_1_, p_235516_0_, p_235516_2_, blockstate, blockpos, Direction.UP, blockstate.getOpacity(p_235516_1_, blockpos));
      return i < p_235516_1_.getMaxLightLevel();
   }

   /**
    * Performs a random tick on a block.
    */
   public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      if (!func_235516_b_(state, worldIn, pos)) {
         worldIn.setBlockState(pos, Refs.infectedGrassBlockState);
      }
   }

   /**
    * Whether this IGrowable can grow
    */
   @SuppressWarnings("deprecation")
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
	   return worldIn.getBlockState(pos.up()).isAir();
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
	   return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
	   ColorVegetationFeature.func_236325_a_(worldIn, rand, pos.up(), Features.Configs.field_243987_k, 3, 1);
   }
   
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
   	
	   if(!worldIn.isRemote) {
		   Utils.infect(worldIn, pos, entityIn);
	   }
	   super.onEntityWalk(worldIn, pos, entityIn);
   }
   
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, (double)pos.getX() + rand.nextDouble(), (double)pos.getY() + 1.1D, (double)pos.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}
}