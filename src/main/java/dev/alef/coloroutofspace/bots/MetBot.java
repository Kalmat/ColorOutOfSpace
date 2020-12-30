package dev.alef.coloroutofspace.bots;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.Utils;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketCured;
import dev.alef.coloroutofspace.playerdata.PlayerData;
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
import net.minecraft.entity.player.ServerPlayerEntity;
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
	
	@SuppressWarnings("deprecation")
	public void metFall(World worldIn, PlayerEntity player, PlayerData playerData) {

		if (playerData.getFallPos() == null) {
			Random rand1 = new Random();
			Random rand2 = new Random();
			int offset = rand1.nextInt(5) + 10;
			playerData.setFallPos(new BlockPos(player.getPositionVec()).offset(Direction.byHorizontalIndex(rand2.nextInt(4)), offset));
		}
		BlockPos pos = playerData.getFallPos();
		
		if (playerData.getMetPos() != null) {
			worldIn.destroyBlock(playerData.getMetPos(), false);
			MetBot metBot = new MetBot();
			metBot.uninfectArea(worldIn, player, playerData.getMetPos(), playerData.getPrevRadius(), false);
		}
		
		worldIn.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), Refs.explosionRadius, true, Explosion.Mode.DESTROY);
		for (int i = 0; i < Refs.explosionRadius; ++i) {
			pos = pos.offset(Direction.DOWN);
			if (!worldIn.getBlockState(pos).isAir()) {
				pos = pos.offset(Direction.UP);
				break;
			}
		}
		worldIn.setBlockState(pos, Refs.meteoriteState);
		worldIn.setBlockState(pos.down(), Refs.infectedGrassBlockState);
		playerData.setMetFallen(true);
		playerData.setMetActive(true);
		playerData.setMetPos(pos);
		playerData.setFallDay(-1);
		playerData.setPlayerCured(false);
		if (Refs.hardcoreMode) {
			playerData.setPlayerInfected(false);
			Networking.sendToClient(new PacketCured(playerData.getPlayerUUID()), (ServerPlayerEntity) playerData.getPlayer());
			playerData.setCureLevel(0);
		}
		this.increaseInfectedArea(worldIn, player, playerData.getMetPos(), 0, Refs.radiusIncrease);
		playerData.setPrevRadius(Refs.radiusIncrease);
	}
	
	public void increaseInfectedArea(World worldIn, PlayerEntity player, BlockPos center, int prevRadius, int radius) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
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
	}
	
	public void infectBlock(World worldIn, BlockPos pos) {
		
		Random rand = new Random();
		BlockState oldState = worldIn.getBlockState(pos);
		BlockState upperState = worldIn.getBlockState(pos.up());
		Block oldBlock = oldState.getBlock();
		Block upperBlock = upperState.getBlock();
		
		if (Refs.dirts.contains(oldBlock) && Utils.hasNotSolidAround(worldIn, pos)) {
			worldIn.setBlockState(pos, Refs.infectedGrassBlockState);
			if (upperBlock instanceof BushBlock && rand.nextInt(Refs.infectedGrassChance) == 0) {
				worldIn.setBlockState(pos.up(), Refs.infectedGrassState);
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
		else if (oldBlock.equals(Blocks.TORCH)) {
			BlockState torchState = Utils.cloneState(oldState, Refs.infectedTorchState);
			worldIn.setBlockState(pos, torchState);
		}
		else if (oldBlock.equals(Blocks.WALL_TORCH)) {
			BlockState torchState = Utils.cloneState(oldState, Refs.infectedWallTorchState);
			worldIn.setBlockState(pos, torchState);
		}
		else if (oldBlock instanceof BedBlock) {
			//BlockState bedState = Refs.infectedBedState.with(HorizontalBlock.HORIZONTAL_FACING, (Direction) oldState.getValues().get(HorizontalBlock.HORIZONTAL_FACING)).with(BlockStateProperties.OCCUPIED, (Boolean) oldState.getValues().get(BlockStateProperties.OCCUPIED));
			BlockState bedState = Utils.cloneState(oldState, Refs.infectedBedState);
			if (!oldBlock.getBlock().equals(Refs.infectedBedState.getBlock()) && oldState.getValues().get(BlockStateProperties.BED_PART).equals(BedPart.FOOT)) {
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
			BlockState doorState = Utils.cloneState(oldState, Refs.infectedDoorState);
			if (!oldBlock.equals(Refs.infectedDoorState.getBlock()) && oldState.getValues().get(BlockStateProperties.DOUBLE_BLOCK_HALF).equals(DoubleBlockHalf.LOWER)) {
				// This MUST BE the exact order of destroy and set actions
				worldIn.destroyBlock(pos, false);
				worldIn.destroyBlock(pos.up(), false);
				worldIn.setBlockState(pos, doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 3);
				worldIn.setBlockState(pos.up(), doorState.with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 3);
			}
		}
		else if (!Refs.modBlockList.contains(oldBlock) && ItemGroup.BUILDING_BLOCKS.equals(oldBlock.asItem().getGroup()) && Utils.hasNotSolidAround(worldIn, pos)) {
			BlockState state = Refs.infectedState;
			if (oldBlock.getDefaultState().getMaterial().equals(Material.WOOD)) {
				state = Refs.infectedWoodState;
			}
			worldIn.setBlockState(pos, state);
		}
	}
	
	public void uninfectArea(World worldIn, PlayerEntity player, BlockPos center, int radius, boolean cured) {
		
		int xx = center.getX();
		int yy = center.getY();
		int zz = center.getZ();
		BlockPos pos;
		
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
