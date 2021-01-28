package dev.alef.coloroutofspace.playerdata;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	
	public int getMetDisableLevel();
	public void setMetDisableLevel(int cureLevel);
	
	public boolean isMetDisabled();
	public void setMetDisabled(boolean metCured);

	public void resetMet(World worldIn, boolean destroyMet, boolean usedAntidote);
	public void resetPlayer(boolean cured);
    public void copyForRespawn(IPlayerData deadPlayer);
    
	public void metFall(World worldIn, PlayerEntity player);
	public void increaseMetRadius(World worldIn);
	public void replanMetFall(int daysJoined);
	public boolean checkMetDisableLevel();
}