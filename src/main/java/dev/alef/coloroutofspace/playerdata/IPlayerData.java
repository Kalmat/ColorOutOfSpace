package dev.alef.coloroutofspace.playerdata;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface IPlayerData {
	
	public BlockPos getMetPos();
	public void setMetPos(BlockPos metPos);
	
	public BlockPos getBedPos();
	public void setBedPos(BlockPos bedPos);
	
	public void setMetFallen(boolean fallen);
	public boolean isMetFallen();
	
	public boolean isPlayerInfected();
	public void setPlayerInfected(boolean playerInfected);
	
	public boolean isPlayerCured();
	public void setPlayerCured(boolean playerCured);
	
	public boolean isMetActive();
	public void setMetActive(boolean metActive);
	
	public int getPrevRadius();
	public void setPrevRadius(int prevRadius);
	
	public long getFirstJoin();
	public void setFirstJoin(long first);
	
	public BlockPos getFallPos();
	public void setFallPos(BlockPos pos, boolean randomize);
	
	public int getFallDay();
	public void setFallDay(int fallDay, boolean randomize);
	
	public int getCureLevel();
	public void setCureLevel(int cureLevel);
	
	public void reset(PlayerEntity player, boolean destroyMet);
    public void copyForRespawn(IPlayerData deadPlayer);

}