package dev.alef.coloroutofspace.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.ColorOutOfSpace;
import dev.alef.coloroutofspace.bot.CalcVector;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MeteoriteBlock extends Block {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Random rand = new Random();
	
    public MeteoriteBlock(Properties properties) {
		super(properties);
    }
    
	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		
		if (placer instanceof PlayerEntity && !worldIn.isRemote && !(((PlayerEntity)placer).isSneaking())) {

			worldIn.destroyBlock(pos, false);
			ColorOutOfSpace.forceMetFall(worldIn, (ServerPlayerEntity) placer, pos);
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	
	//CLIENT
	@Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

		int effects = 3;
		List<Double> d = new ArrayList<Double>();
		for (int i = 0; i < effects; ++i) {
			d = CalcVector.randomSpherePoint(((double) pos.getX()) + 0.5D, ((double) pos.getY() +0.5D), ((double) pos.getZ()) + 0.5D, 0.7D);
			worldIn.addParticle(ParticleTypes.PORTAL, d.get(0), d.get(1), d.get(2), (this.rand.nextDouble() - 0.5D) * 2.0D, this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0);
		}
		worldIn.addParticle(ParticleTypes.FLASH, ((double) pos.getX()) + 0.5D, ((double) pos.getY()) + 0.5D, ((double) pos.getZ()) + 0.5D, 0.0D, 0.0D, 0.0D);
		super.animateTick(stateIn, worldIn, pos, rand);
	}

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
    	
    	if(!worldIn.isRemote) {
    		ColorOutOfSpace.Infection.infectEntity(worldIn, entityIn);
    	}
    	super.onEntityWalk(worldIn, pos, entityIn);
    }
}
