package dev.alef.coloroutofspace.playerdata;

import java.util.Random;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketCured;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PlayerData implements IPlayerData {
	
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
	
	public PlayerData() {
		this.setFirstJoin(0);
		this.setBedPos(null);
		this.setFallDay(-1, false);
		this.setFallPos(null, false);
		this.setMetPos(null);
		this.setMetFallen(false);
		this.setMetActive(false);
		this.setPrevRadius(0);
		this.setPlayerInfected(false);
		this.setCureLevel(0);
		this.setPlayerCured(false);
	}
	
	public long getFirstJoin() {
		return this.firstJoin;
	}

	public void setFirstJoin(long first) {
		this.firstJoin = first;
	}
	
	public BlockPos getBedPos() {
		return bedPos;
	}

	public void setBedPos(BlockPos bedPos) {
		this.bedPos = bedPos;
	}

	public int getFallDay() {
		return fallDay;
	}

	public void setFallDay(int fallDay, boolean randomize) {
		Random rand = new Random();
		this.fallDay = fallDay + rand.nextInt(Refs.graceDaysToFall);
	}

	public BlockPos getFallPos() {
		return fallPos;
	}

	public void setFallPos(BlockPos pos, boolean randomize) {
		if (randomize && fallPos != null) {
			Random rand1 = new Random();
			Random rand2 = new Random();
			int offset = rand1.nextInt(10) + 5;
			pos = new BlockPos(pos.offset(Direction.byHorizontalIndex(rand2.nextInt(4)), offset));
		}
		this.fallPos = pos;
	}

	public BlockPos getMetPos() {
		return this.metPos;
	}

	public void setMetPos(BlockPos metPos) {
		this.metPos = metPos;
	}

	public void setMetFallen(boolean fallen) {
		this.metFallen = fallen;
	}
	
	public boolean isMetFallen() {
		return this.metFallen;
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

	public boolean isPlayerInfected() {
		return playerInfected;
	}

	public void setPlayerInfected(boolean playerInfected) {
		this.playerInfected = playerInfected;
	}

	public int getCureLevel() {
		return cureLevel;
	}

	public void setCureLevel(int cureLevel) {
		this.cureLevel = cureLevel;
	}

	public boolean isPlayerCured() {
		return playerCured;
	}

	public void setPlayerCured(boolean playerCured) {
		this.playerCured = playerCured;
	}

	public void reset(PlayerEntity player, boolean destroyMet) {
		
		if (destroyMet && this.getMetPos() != null) {
			player.world.destroyBlock(this.getMetPos(), false);
		}
		this.setFirstJoin(0);
		this.setFallPos(null, false);
		this.setFallDay(-1, false);
		this.setMetPos(null);
		this.setMetFallen(false);
		this.setMetActive(false);
		this.setPrevRadius(0);
		this.setPlayerInfected(false);
		this.setCureLevel(0);
		this.setPlayerCured(false);
		Networking.sendToClient(new PacketCured(player.getUniqueID()), (ServerPlayerEntity) player);
	}
	
    public static IPlayerData getFromPlayer(PlayerEntity player) {
        return player
                .getCapability(PlayerDataProvider.ColorOutOfSpaceStateCap, null)
                .orElseThrow(()->new IllegalArgumentException("LazyOptional must be not empty!"));
    }

    @Override
    public void copyForRespawn(IPlayerData deadPlayer){

        this.setFirstJoin(deadPlayer.getFirstJoin());
        this.setBedPos(deadPlayer.getBedPos());
        this.setFallPos(deadPlayer.getFallPos(), false);
        this.setFallDay(deadPlayer.getFallDay(), false);
        this.setMetPos(deadPlayer.getMetPos());
        this.setMetFallen(deadPlayer.isMetFallen());
        this.setMetActive(deadPlayer.isMetActive());
        this.setPrevRadius(deadPlayer.getPrevRadius());
        this.setPlayerInfected(deadPlayer.isPlayerInfected());
        this.setCureLevel(deadPlayer.getCureLevel());
        this.setPlayerCured(deadPlayer.isPlayerCured());
    }
}
