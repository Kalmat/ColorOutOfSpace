package dev.alef.coloroutofspace.blocks;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NetherVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVineFeature;
import net.minecraft.world.server.ServerWorld;

public class InfectedDirtBlock extends Block implements IGrowable {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	public InfectedDirtBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    	
    	if(!worldIn.isRemote) {
    		Utils.infect(worldIn, pos, entityIn);
    	}
    	super.onEntityWalk(worldIn, pos, entityIn);
    }

	@SuppressWarnings("deprecation")
	@Override
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
	      return worldIn.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		BlockState blockstate = worldIn.getBlockState(pos);
		BlockPos blockpos = pos.up();
		if (blockstate.isIn(Blocks.field_235381_mu_)) {
			NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.field_243987_k, 3, 1);
		} else if (blockstate.isIn(Blocks.field_235372_ml_)) {
			NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.field_243988_l, 3, 1);
			NetherVegetationFeature.func_236325_a_(worldIn, rand, blockpos, Features.Configs.field_243989_m, 3, 1);
			if (rand.nextInt(8) == 0) {
				TwistingVineFeature.func_236423_a_(worldIn, rand, blockpos, 3, 1, 2);
			}
		}
	}
}