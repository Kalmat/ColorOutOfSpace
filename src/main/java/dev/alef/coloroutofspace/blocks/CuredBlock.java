package dev.alef.coloroutofspace.blocks;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuredBlock extends Block {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
    public CuredBlock(Properties properties) {
		super(properties);
    }
    
	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
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
		int type = rand1.nextInt(Math.max(6, Refs.lootChance));

		if (type == 0 || type == 1 || type == 2) {  // Item
			
			Item spawnItem = Refs.lootItems.get(rand2.nextInt(Refs.lootItems.size()));

			if (spawnItem.equals(Items.POTION) || spawnItem.equals(Items.SPLASH_POTION)) {
				Utils.spawnPotion(worldIn, player, pos, spawnItem);
			}
			else if (spawnItem.equals(Items.FILLED_MAP)) {
				Utils.spawnMap(worldIn, player, pos, spawnItem);
			}
			else {
				Utils.spawnItem(worldIn, player, pos, spawnItem, true, true);
			}
		}
		else if (type == 3 || type == 4) {  // Entity
			
			EntityType<?> spawnEntity = Refs.lootEntities.get(rand2.nextInt(Refs.lootEntities.size()));
			Utils.spawnEntity(worldIn, player, pos, spawnEntity, true);
		}
		else if (type == 5) { // Water or Lava
			
			Fluid spawnBlock = Refs.nonLootBlocks.get(rand2.nextInt(Refs.nonLootBlocks.size()));
			Utils.spawnFluid(worldIn, player, pos, spawnBlock);
  		}
		else if (Refs.spawnDefaultLootEntity) { // Default (lepisma or nothing)
			
			if (rand2.nextInt(Refs.spawnDefaultLootChance) == 0) {
				EntityType<?> spawnEntity = Refs.defaultLootEntity;
				Utils.spawnEntity(worldIn, player, pos, spawnEntity, false);
			}
		}
	}
}
