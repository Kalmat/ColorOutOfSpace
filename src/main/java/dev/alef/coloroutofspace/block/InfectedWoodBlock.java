package dev.alef.coloroutofspace.block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfectedWoodBlock extends Block {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	public InfectedWoodBlock(Properties properties) {
		super(properties);
	}

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    	
    	if(!worldIn.isRemote) {
    		Util.infect(worldIn, pos, entityIn);
    	}
    	super.onEntityWalk(worldIn, pos, entityIn);
    }
}
