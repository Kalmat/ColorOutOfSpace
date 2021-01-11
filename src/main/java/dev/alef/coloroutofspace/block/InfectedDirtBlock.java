package dev.alef.coloroutofspace.block;

import java.util.Random;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Util;

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
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.server.ServerWorld;

public class InfectedDirtBlock extends Block implements IGrowable {
   public InfectedDirtBlock(AbstractBlock.Properties properties) {
      super(properties);
   }

   private static boolean func_235516_b_(BlockState p_235516_0_, IWorldReader p_235516_1_, BlockPos p_235516_2_) {
      BlockPos blockpos = p_235516_2_.up();
      BlockState blockstate = p_235516_1_.getBlockState(blockpos);
      int i = LightEngine.func_215613_a(p_235516_1_, p_235516_0_, p_235516_2_, blockstate, blockpos, Direction.UP, blockstate.getOpacity(p_235516_1_, blockpos));
      return i < p_235516_1_.getMaxLightLevel();
   }

   public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      if (func_235516_b_(state, worldIn, pos)) {
         this.grow(worldIn, random, pos, state);
      }
   }

   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
	   return worldIn.getBlockState(pos.up()).propagatesSkylightDown(worldIn, pos);
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
	   worldIn.setBlockState(pos, Refs.infectedGrassBlockState, 3);
   }
   
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
	   	
	   if (!worldIn.isRemote) {
		   Util.infect(worldIn, pos, entityIn);
	   }
	   super.onEntityWalk(worldIn, pos, entityIn);
   }
   
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	   if (rand.nextInt(10) == 0) {
		   worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, (double)pos.getX() + rand.nextDouble(), (double)pos.getY() + 1.1D, (double)pos.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
	   }
   }
}
