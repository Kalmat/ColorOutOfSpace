package dev.alef.coloroutofspace.bot;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Util;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.BushBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class MetBot {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	public MetBot() {
	}
	
	public BlockPos dropMet(World worldIn, BlockPos fallPos) {
		
		fallPos = Util.getGroundLevel(worldIn, fallPos, false);
		boolean fire = (Refs.difficulty == Refs.HARDCORE);
		worldIn.createExplosion(null, fallPos.getX(), fallPos.getY(), fallPos.getZ(), Refs.explosionRadius, fire, Explosion.Mode.DESTROY);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fallPos = Util.getGroundLevel(worldIn, fallPos, true);
		this.infectArea(worldIn, fallPos, 0, Refs.radiusIncrease, false);
		worldIn.setBlockState(fallPos, Refs.meteoriteState);
		
		return fallPos;
	}
	
	public void infectArea(World worldIn, BlockPos center, int prevRadius, int radius, boolean makeJail) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
		for (int z = -radius; z < radius; ++z) {
			for (int y = -radius; y < radius; ++y) {
				for (int x = -radius; x < radius; ++x) {
					if (x*x + y*y + z*z >= prevRadius*prevRadius && x*x + y*y + z*z <= radius*radius) {
						pos = new BlockPos(xx + x, yy + y, zz + z);
						this.infectBlock(worldIn, pos, makeJail);
					}
				}
			}
		}
	}
	
	public void infectBlock(World worldIn, BlockPos pos, boolean makeJail) {
		
		Random rand = new Random();
		BlockState oldState = worldIn.getBlockState(pos);
		BlockState upperState = worldIn.getBlockState(pos.up());
		Block oldBlock = oldState.getBlock();
		Block upperBlock = upperState.getBlock();
		
		if (makeJail) {
			 if (!oldState.isSolid()) {
				worldIn.setBlockState(pos, Refs.infectedGlassState);
			}
		}
		else if (!Refs.modBlockList.contains(oldBlock)) {
			if ((oldState.getMaterial().equals(Material.EARTH) || oldState.getMaterial().equals(Material.ORGANIC)) &&
					ItemGroup.BUILDING_BLOCKS.equals(oldBlock.asItem().getGroup()) && Util.hasNotSolidAround(worldIn, pos)) {
				worldIn.setBlockState(pos, Refs.infectedGrassBlockState);
				if (upperBlock instanceof BushBlock && rand.nextInt(Refs.infectedGrassChance) == 0) {
					worldIn.setBlockState(pos.up(), Refs.infectedGrassState);
				}
			}
			else if (oldBlock instanceof LeavesBlock) {
				worldIn.setBlockState(pos, Refs.infectedLeavesState);
			}
			else if (oldBlock instanceof GlassBlock) {
				worldIn.setBlockState(pos, Refs.infectedGlassState);
			}
			else if (oldBlock instanceof PaneBlock) {
				BlockState paneState = Util.cloneState(oldState, Refs.infectedGlassPaneState);
				worldIn.setBlockState(pos, paneState);
			}
			else if (oldBlock instanceof StairsBlock) {
				BlockState stairState = Util.cloneState(oldState, Refs.infectedStairsState);
				worldIn.setBlockState(pos, stairState);
			}
			else if (oldBlock instanceof SlabBlock) {
				BlockState slabState = Util.cloneState(oldState, Refs.infectedSlabState);
				worldIn.setBlockState(pos, slabState);
			}
			else if (oldBlock.equals(Blocks.TORCH)) {
				BlockState torchState = Util.cloneState(oldState, Refs.infectedTorchState);
				worldIn.setBlockState(pos, torchState);
			}
			else if (oldBlock.equals(Blocks.WALL_TORCH)) {
				BlockState wallTorchState = Util.cloneState(oldState, Refs.infectedWallTorchState);
				worldIn.setBlockState(pos, wallTorchState);
			}
			else if (oldBlock instanceof BedBlock) {
				//BlockState bedState = Refs.infectedBedState.with(HorizontalBlock.HORIZONTAL_FACING, (Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)).with(BlockStateProperties.OCCUPIED, (Boolean) oldState.getValues().get(BlockStateProperties.OCCUPIED));
				BlockState bedState = Util.cloneState(oldState, Refs.infectedBedState);
				if (!oldBlock.equals(Refs.infectedBedState.getBlock()) && oldState.getValues().get(BlockStateProperties.BED_PART).equals(BedPart.FOOT)) {
					// This MUST BE the exact order of destroy and set actions
			        BlockPos blockpos = pos.offset(((Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)));
					worldIn.destroyBlock(blockpos, false);
					worldIn.setBlockState(blockpos, bedState.with(BlockStateProperties.BED_PART, BedPart.HEAD), 3);
					worldIn.destroyBlock(pos, false);
					worldIn.setBlockState(pos, bedState.with(BlockStateProperties.BED_PART, BedPart.FOOT), 3);
				}
			}
			else if (oldBlock instanceof DoorBlock) {
				//BlockState doorState = Refs.infectedDoorState.with(HorizontalBlock.HORIZONTAL_FACING, (Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)).with(BlockStateProperties.DOOR_HINGE, (DoorHingeSide) oldState.getValues().get(BlockStateProperties.DOOR_HINGE)).with(BlockStateProperties.POWERED, false).with(BlockStateProperties.OPEN, false);
				BlockState doorState = Util.cloneState(oldState, Refs.infectedDoorState);
				if (!oldBlock.equals(Refs.infectedDoorState.getBlock()) && oldState.getValues().get(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
					// This MUST BE the exact order of destroy and set actions	
					worldIn.destroyBlock(pos, false);
					worldIn.destroyBlock(pos.up(), false);
					worldIn.setBlockState(pos, doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 3);
					worldIn.setBlockState(pos.up(), doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
				}
			}
			else if (ItemGroup.BUILDING_BLOCKS.equals(oldBlock.asItem().getGroup()) &&
					 !(oldBlock instanceof BreakableBlock) && Util.hasNotSolidAround(worldIn, pos)) {
				worldIn.setBlockState(pos, oldBlock.getDefaultState().getMaterial().equals(Material.WOOD) ? Refs.infectedWoodState : Refs.infectedState);
			}
		}
	}
	
	public void uninfectArea(World worldIn, BlockPos center, int radius, boolean cured) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
		worldIn.destroyBlock(center, false);
		
		for (int z = -radius; z < radius; ++z) {
			for (int y = -radius; y < radius; ++y) {
				for (int x = -radius; x < radius; ++x) {
					if (x*x + y*y + z*z <= radius*radius) {	
						pos = new BlockPos(xx + x, yy + y, zz + z);
						this.uninfectBlock(worldIn, pos, cured);
					}
				}
			}
		}
	}
	
	public void uninfectBlock(World worldIn, BlockPos pos, boolean cured) {
		
		Block oldBlock = worldIn.getBlockState(pos).getBlock();
		 
		if (Refs.modBlocksToCureList.contains(oldBlock)) {
			worldIn.setBlockState(pos, cured ? Refs.curedState : Refs.nonCuredState);
		}
	}
}
