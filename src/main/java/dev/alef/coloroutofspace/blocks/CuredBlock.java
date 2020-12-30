package dev.alef.coloroutofspace.blocks;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuredBlock extends Block {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	public CuredBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)  {
		if (!worldIn.isRemote) {
			this.spawnLoot(worldIn, pos, state, player);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
	
	private void spawnLoot(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		
		Random rand1 = new Random();
		Random rand2 = new Random();
		int type = rand1.nextInt(Math.max(7, Refs.lootChance));
		
		if (type == 0 || type == 1 || type == 2 || type == 3) {  // Item

			Item spawnItem = Refs.lootItems.get(rand2.nextInt(Refs.lootItems.size()));
			Utils.spawnItem(worldIn, player, pos, spawnItem, true, true, Refs.enchantabilityChance);
		}
		else if (type == 4 || type == 5) {  // Entity
			
			EntityType<?> spawnEntity = Refs.lootEntities.get(rand2.nextInt(Refs.lootEntities.size()));
			Utils.spawnEntity(worldIn, player, pos, spawnEntity, true, null);
		}
		else if (type == 6) { // Water or Lava
			
			Fluid spawnFluid = Refs.nonLootBlocks.get(rand2.nextInt(Refs.nonLootBlocks.size()));
			Utils.spawnFluid(worldIn, player, pos, spawnFluid);
  		}
		else if (Refs.spawnDefaultLootEntity) { // Default (lepisma or nothing)
			
			if (rand2.nextInt(Refs.spawnDefaultLootChance) == 0) {
				EntityType<?> spawnEntity = Refs.defaultLootEntity;
				Utils.spawnEntity(worldIn, player, pos, spawnEntity, false, null);
			}
		}
	}
	
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(10) == 0) {
			worldIn.addParticle(ParticleTypes.INSTANT_EFFECT, (double)pos.getX() + rand.nextDouble(), (double)pos.getY() + 1.1D, (double)pos.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}
}