package dev.alef.coloroutofspace.block;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.ColorOutOfSpace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfectedBlock extends Block {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	public InfectedBlock(Properties properties) {
		super(properties);
	}

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    	
    	if(!worldIn.isRemote) {
    		ColorOutOfSpace.Infection.infectEntity(worldIn, entityIn);
    	}
    	super.onEntityWalk(worldIn, pos, entityIn);
    }
    
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, (double)pos.getX() + rand.nextDouble(), (double)pos.getY() + 1.1D, (double)pos.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}
}
