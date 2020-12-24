package dev.alef.coloroutofspace.bots;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MetBot {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
	public MetBot() {
	}
	
	public void increaseInfectedArea(World worldIn, PlayerEntity player, BlockPos center, int prevRadius, int radius) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
		ColorOutOfSpaceRender.stopSound();
		for (int z = -radius; z < radius; ++z) {
			for (int y = -radius; y < radius; ++y) {
				for (int x = -radius; x < radius; ++x) {
					if (x*x + y*y + z*z >= prevRadius*prevRadius && x*x + y*y + z*z <= radius*radius) {
						pos = new BlockPos(xx + x, yy + y, zz + z);
						this.infectBlock(worldIn, pos);
					}
				}
			}
		}
		ColorOutOfSpaceRender.resumeSound();
	}
	
	public void infectBlock(World worldIn, BlockPos pos) {
		
		BlockState oldState = worldIn.getBlockState(pos);
		Block oldBlock = oldState.getBlock();
		Random rand = new Random();
		
		if (Refs.dirts.contains(oldBlock)) {
			if (worldIn.getBlockState(pos.up()).getBlock() instanceof BushBlock && rand.nextInt(3) == 0) {
				worldIn.setBlockState(pos, Refs.infectedGrassBlockState);
				worldIn.setBlockState(pos.up(), Refs.infectedGrassState);
			}
			else {
				worldIn.setBlockState(pos, Refs.infectedDirtState);
			}
		}
		else if (oldBlock instanceof LeavesBlock) {
			worldIn.setBlockState(pos, Refs.infectedLeavesState);
		}
		else if (oldBlock.equals(Blocks.GLASS)) {
			worldIn.setBlockState(pos, Refs.infectedGlassState);
		}
		else if (oldBlock.equals(Blocks.GLASS_PANE)) {
			worldIn.setBlockState(pos, Refs.infectedGlassPaneState);
		}
		else if (oldBlock instanceof BedBlock) {
			BlockState bedState = Refs.infectedBedState.with(HorizontalBlock.HORIZONTAL_FACING, (Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)).with(BlockStateProperties.OCCUPIED, (boolean) oldState.getValues().get(BlockStateProperties.OCCUPIED));
			if (!oldBlock.getBlock().equals(Refs.infectedBedState.getBlock()) && oldState.getValues().get(BlockStateProperties.BED_PART).equals(BedPart.FOOT)) {
		        BlockPos blockpos = pos.offset(((Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)));
				worldIn.destroyBlock(blockpos, false);
				worldIn.setBlockState(blockpos, bedState.with(BlockStateProperties.BED_PART, BedPart.HEAD), 3);
				worldIn.destroyBlock(pos, false);
				worldIn.setBlockState(pos, bedState.with(BlockStateProperties.BED_PART, BedPart.FOOT), 3);
			}
		}
		else if (oldBlock.getDefaultState().getMaterial().equals(Material.WOOD)) {
			if (oldBlock instanceof DoorBlock) {
				BlockState doorState = Refs.infectedDoorState.with(HorizontalBlock.HORIZONTAL_FACING, (Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)).with(BlockStateProperties.DOOR_HINGE, (DoorHingeSide) oldState.getValues().get(BlockStateProperties.DOOR_HINGE)).with(BlockStateProperties.POWERED, false).with(BlockStateProperties.OPEN, false);
				if (!oldBlock.getBlock().equals(Refs.infectedDoorState.getBlock()) && oldState.getValues().get(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
					worldIn.destroyBlock(pos, false);
					worldIn.destroyBlock(pos.up(), false);
					worldIn.setBlockState(pos, doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 3);
					worldIn.setBlockState(pos.up(), doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
				}
			}
			else {
				worldIn.setBlockState(pos, Refs.infectedWoodState);
			}
		}
		else if (!Refs.infectNotStateList.contains(oldBlock) && !(oldBlock instanceof DoorBlock) && !(oldBlock instanceof BedBlock)) {
			worldIn.setBlockState(pos, Refs.infectedState);
		}
	}
	
	public void uninfectArea(World worldIn, PlayerEntity player, BlockPos center, int radius) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
		ColorOutOfSpaceRender.stopSound();
		for (int z = -radius; z < radius; ++z) {
			for (int y = -radius; y < radius; ++y) {
				for (int x = -radius; x < radius; ++x) {
					if (x*x + y*y + z*z <= radius*radius) {
						pos = new BlockPos(xx + x, yy + y, zz + z);
						this.uninfectBlock(worldIn, pos);
					}
				}
			}
		}
		ColorOutOfSpaceRender.resumeSound();
	}
	
	public void uninfectBlock(World worldIn, BlockPos pos) {
		
		BlockState oldState = worldIn.getBlockState(pos);
		 
		if (oldState.equals(Refs.infectedState) || oldState.equals(Refs.infectedWoodState) || oldState.equals(Refs.infectedDirtState)) {
			worldIn.setBlockState(pos, Refs.curedState);
		}
	}
}
