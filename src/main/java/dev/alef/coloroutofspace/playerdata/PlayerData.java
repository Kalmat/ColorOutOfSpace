package dev.alef.coloroutofspace.playerdata;

import java.util.UUID;

import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketCured;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerData {
	
	private World currentWorld;
	private PlayerEntity currentPlayer;
	private UUID currentPlayerUUID;
	private BlockPos metPos;
	private BlockPos bedPos;
	private BlockPos fallPos;
	private long firstJoin;
	private int fallDay;
	private boolean metFallen;
	private boolean playerInfected;
	private boolean playerCured;
	private boolean metActive;
	private int prevRadius;
	private int cureLevel;
	
	PlayerData(World worldIn, PlayerEntity player) {
		this.currentWorld = worldIn;
		this.currentPlayer = player;
		this.currentPlayerUUID = player.getUniqueID();
		this.setMetPos(null);
		this.setBedPos(null);
		this.setFallPos(null);
		this.setFallDay(-1);
		this.setMetFallen(false);
		this.setPlayerInfected(false);
		this.setPlayerCured(false);
		this.setMetActive(false);
		this.setPrevRadius(0);
		this.setFirstJoin(0);
		this.setCureLevel(0);
	}
	
	public World getWorld() {
		return this.currentWorld;
	}
	
	public PlayerEntity getPlayer() {
		return this.currentPlayer;
	}
	
	public UUID getPlayerUUID() {
		return this.currentPlayerUUID;
	}
	
	public BlockPos getMetPos() {
		return this.metPos;
	}

	public void setMetPos(BlockPos metPos) {
		this.metPos = metPos;
	}

	public BlockPos getBedPos() {
		return bedPos;
	}

	public void setBedPos(BlockPos bedPos) {
		this.bedPos = bedPos;
	}

	public void setMetFallen(boolean fallen) {
		this.metFallen = fallen;
	}
	
	public boolean isMetFallen() {
		return this.metFallen;
	}

	public boolean isPlayerInfected() {
		return playerInfected;
	}

	public void setPlayerInfected(boolean playerInfected) {
		this.playerInfected = playerInfected;
	}

	public boolean isPlayerCured() {
		return playerCured;
	}

	public void setPlayerCured(boolean playerCured) {
		this.playerCured = playerCured;
	}

	public boolean isMetActive() {
		return metActive;
	}

	public void setMetActive(boolean metActive) {
		this.metActive = metActive;
	}

	public int getPrevRadius() {
		return prevRadius;
	}

	public void setPrevRadius(int prevRadius) {
		this.prevRadius = prevRadius;
	}

	public long getFirstJoin() {
		return this.firstJoin;
	}

	public void setFirstJoin(long first) {
		this.firstJoin = first;
	}
	
	public BlockPos getFallPos() {
		return fallPos;
	}

	public void setFallPos(BlockPos fallPos) {
		this.fallPos = fallPos;
	}

	public int getFallDay() {
		return fallDay;
	}

	public void setFallDay(int fallDay) {
		this.fallDay = fallDay;
	}

	public int getCureLevel() {
		return cureLevel;
	}

	public void setCureLevel(int cureLevel) {
		this.cureLevel = cureLevel;
	}

	public void reset(boolean destroyMet) {
		
		if (destroyMet && this.getMetPos() != null) {
			this.getWorld().destroyBlock(this.getMetPos(), false);
		}
		this.setMetPos(null);
		this.setFallPos(null);
		this.setFallDay(-1);
		this.setMetFallen(false);
		this.setPlayerInfected(false);
		this.setPlayerCured(false);
		this.setMetActive(false);
		this.setPrevRadius(0);
		this.setFirstJoin(0);
		this.setCureLevel(0);
		Networking.sendToClient(new PacketCured(this.currentPlayerUUID), (ServerPlayerEntity) this.currentPlayer);
	}
}
