package dev.alef.coloroutofspace.blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CuredMetBlock extends Block {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
    public CuredMetBlock(Properties properties) {
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
}
