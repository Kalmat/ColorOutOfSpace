package dev.alef.coloroutofspace.blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class InfectedBlock extends Block {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	public InfectedBlock(Properties properties) {
		super(properties);
	}

	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
    
	@Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)  {
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
    	super.onPlayerDestroy(worldIn, pos, state);
    }
    
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
    	super.onExplosionDestroy(worldIn, pos, explosionIn);
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    	
    	if(!worldIn.isRemote) {
    		Utils.infect(worldIn, pos, entityIn);
    	}
    	super.onEntityWalk(worldIn, pos, entityIn);
    }
}
