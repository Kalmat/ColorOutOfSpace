package dev.alef.coloroutofspace.playerdata;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.bot.MetBot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PlayerData implements IPlayerData {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	
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
	private int metDisableLevel;
	private boolean metDisabled;
	
	public PlayerData() {
		this.setFirstJoin(0);
		this.setFallDay(-1, false);
		this.setBedPos(null);
		this.setFallPos(null, false);
		this.setMetPos(null);
		this.setMetFallen(false);
		this.setMetActive(false);
		this.setPrevRadius(0);
		this.setMetDisableLevel(0);
		this.setMetDisabled(false);
		this.setPlayerInfected(false);
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
		this.fallDay = fallDay;
		if (randomize) {
			Random rand = new Random();
			this.fallDay += rand.nextInt(Refs.graceDaysToFall);
		}
	}

	public BlockPos getFallPos() {
		return fallPos;
	}

	public void setFallPos(BlockPos pos, boolean randomize) {
		if (randomize && pos != null) {
			Random rand1 = new Random();
			Random rand2 = new Random();
			int offset = rand1.nextInt(5) + 7;
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

	public int getMetDisableLevel() {
		return metDisableLevel;
	}

	public void setMetDisableLevel(int disableLevel) {
		this.metDisableLevel = disableLevel;
	}

	public boolean isMetDisabled() {
		return metDisabled;
	}

	public void setMetDisabled(boolean metCured) {
		this.metDisabled = metCured;
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

	public boolean canMetFall(World worldIn, int daysJoined) {
		return daysJoined >= this.getFallDay() && this.getFallDay() > 0 && 
				!this.isMetActive() && !this.isMetDisabled() &&
				!this.isPlayerCured() && (!this.isPlayerInfected() || Refs.difficulty == Refs.HARDCORE) &&
				worldIn.getDimensionKey().equals(World.OVERWORLD);
	}

	public void metFall(World worldIn, PlayerEntity player) {
		
		if (this.getFallPos() == null) {
			this.setFallPos(player.getPosition(), true);
		}
		MetBot metBot = new MetBot();
		BlockPos fallPos = metBot.dropMet(worldIn, this.getFallPos());
		this.resetMet(worldIn, false, false);
		this.setMetPos(fallPos);
		this.setMetFallen(true);
		this.setMetActive(true);
		this.setPrevRadius(Refs.radiusIncrease);
	}
	
	public void increaseMetRadius(World worldIn) {
		int radius = this.getPrevRadius() + Refs.radiusIncrease;
		MetBot metBot = new MetBot();
		metBot.infectArea(worldIn, this.getMetPos(), this.getPrevRadius(), radius, false);
		this.setPrevRadius(radius);
	}
	
	public void replanMetFall(int daysJoined) {
		this.setMetActive(false);
		this.setFallDay(daysJoined + Refs.daysToFall, true);
	}
    
	public boolean checkMetDisableLevel() {
		
		this.setMetDisableLevel(this.getMetDisableLevel() + 1);
		boolean justMetCured = false;

		if (this.getMetDisableLevel() == Refs.cureMaxLevel) {
			this.setMetActive(false);
			this.setMetDisabled(true);
			justMetCured = true;
		}
		return justMetCured;
	}

	public void resetMet(World worldIn, boolean destroyMet, boolean usedAntidote) {
		
		if (this.getMetPos() != null) {
			MetBot metBot = new MetBot();
			metBot.uninfectArea(worldIn, this.getMetPos(), this.getPrevRadius(), usedAntidote);
		}
		if (destroyMet) {
			this.setMetPos(null);
		}
		this.setMetFallen(false);
		this.setMetActive(false);
		this.setFallDay(-1, false);
		this.setPrevRadius(0);
		this.setMetDisabled(false);
	}
	
	public void resetPlayer(boolean cured) {
		this.setPlayerInfected(false);
		this.setMetDisableLevel(0);
		this.setPlayerCured(cured);
	}
	
    @Override
    public void copyForRespawn(IPlayerData deadPlayer){
        this.setFirstJoin(deadPlayer.getFirstJoin());
        this.setFallDay(deadPlayer.getFallDay(), false);
        this.setBedPos(deadPlayer.getBedPos());
        this.setFallPos(deadPlayer.getFallPos(), false);
        this.setMetPos(deadPlayer.getMetPos());
        this.setMetFallen(deadPlayer.isMetFallen());
        this.setMetActive(deadPlayer.isMetActive());
        this.setPrevRadius(deadPlayer.getPrevRadius());
        this.setMetDisableLevel(deadPlayer.getMetDisableLevel());
		this.setMetDisabled(deadPlayer.isMetDisabled());
        this.setPlayerInfected(deadPlayer.isPlayerInfected());
        this.setPlayerCured(deadPlayer.isPlayerCured());
    }
    
    public CompoundNBT createNBT() {
    	
        CompoundNBT tag = new CompoundNBT();
        
        if (this.getMetPos() != null) {
        	tag.putLong("MP", this.getMetPos().toLong());
        }
        if (this.getBedPos() != null) {
            tag.putLong("BP", this.getBedPos().toLong());
        }
        if (this.getFallPos() != null) {
        	tag.putLong("FP", this.getFallPos().toLong());
        }
        tag.putLong("FJ", this.getFirstJoin());
        tag.putInt("FD", this.getFallDay());
        tag.putBoolean("MF", this.isMetFallen());
        tag.putBoolean("MA", this.isMetActive());
        tag.putInt("PR", this.getPrevRadius());
        tag.putInt("SC", this.getMetDisableLevel());
        tag.putBoolean("MD", this.isMetDisabled());
        tag.putBoolean("PI", this.isPlayerInfected());
        tag.putBoolean("PC", this.isPlayerCured());
        
        return tag;
    }

    public IPlayerData retrieveNBT(CompoundNBT tag) {
    	
        long longvalue = tag.getLong("MP");
        if (longvalue != 0L) {
        	this.setMetPos(BlockPos.fromLong(longvalue));
        }
        else {
        	this.setMetPos(null);
        }
        longvalue = tag.getLong("BP");
        if (longvalue != 0L) {
        	this.setBedPos(BlockPos.fromLong(longvalue));
        }
        else {
        	this.setBedPos(null);
        }
        longvalue = tag.getLong("FP");
        if (longvalue != 0L) {
        	this.setFallPos(BlockPos.fromLong(longvalue), false);
        }
        else {
        	this.setFallPos(null, false);
        }
        this.setFirstJoin(tag.getLong("FJ"));
        boolean metFallen = tag.getBoolean("MF");
        this.setMetFallen(metFallen);
        int intvalue = tag.getInt("FD");
        if (intvalue != 0 || !metFallen) {
        	this.setFallDay(intvalue, false);
        }
        else {
        	this.setFallDay(-1, false);
        }
        this.setMetActive(tag.getBoolean("MA"));
        this.setPrevRadius(tag.getInt("PR"));
        this.setMetDisableLevel(tag.getInt("SC"));
        this.setMetDisabled(tag.getBoolean("MD"));
        this.setPlayerInfected(tag.getBoolean("PI"));
        this.setPlayerCured(tag.getBoolean("PC"));
        
        return this;
    }
    
    public static IPlayerData getFromPlayer(PlayerEntity player) {
        return player
                .getCapability(PlayerDataProvider.ColorOutOfSpaceStateCap, null)
                .orElseThrow(() -> new IllegalArgumentException("LazyOptional Capability must not be empty! "+player.getScoreboardName()));
    }
    
	public static void registerPlayerCapability() {
    	CapabilityManager.INSTANCE.register(IPlayerData.class, new PlayerDataStorage(), PlayerData::new);
    }
}
