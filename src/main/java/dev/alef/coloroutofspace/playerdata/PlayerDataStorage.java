package dev.alef.coloroutofspace.playerdata;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerDataStorage implements Capability.IStorage<IPlayerData> {
	
    @Nullable
    @Override
    public INBT writeNBT(Capability<IPlayerData> capability, IPlayerData instance, Direction side) {
    	
        CompoundNBT tag = new CompoundNBT();
        
        if (instance.getMetPos() != null) {
        	tag.putLong("MP", instance.getMetPos().toLong());
        }
        if (instance.getBedPos() != null) {
            tag.putLong("BP", instance.getBedPos().toLong());
        }
        if (instance.getFallPos() != null) {
        	tag.putLong("FP", instance.getFallPos().toLong());
        }
        tag.putLong("FJ", instance.getFirstJoin());
        tag.putInt("FD", instance.getFallDay());
        tag.putBoolean("MF", instance.isMetFallen());
        tag.putBoolean("PI", instance.isPlayerInfected());
        tag.putBoolean("MA", instance.isMetActive());
        tag.putInt("PR", instance.getPrevRadius());
        tag.putInt("SC", instance.getMetDisableLevel());
        tag.putBoolean("PC", instance.isPlayerCured());
        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, Direction side, INBT nbt) {

    	CompoundNBT tag = (CompoundNBT)nbt;

        long longvalue = tag.getLong("MP");
        if (longvalue != 0L) {
        	instance.setMetPos(BlockPos.fromLong(longvalue));
        }
        else {
            instance.setMetPos(null);
        }
        longvalue = tag.getLong("BP");
        if (longvalue != 0L) {
        	instance.setBedPos(BlockPos.fromLong(longvalue));
        }
        else {
            instance.setBedPos(null);
        }
        longvalue = tag.getLong("FP");
        if (longvalue != 0L) {
        	instance.setFallPos(BlockPos.fromLong(longvalue), false);
        }
        else {
            instance.setFallPos(null, false);
        }
        instance.setFirstJoin(tag.getLong("FJ"));
        boolean metFallen = tag.getBoolean("MF");
        instance.setMetFallen(metFallen);
        int intvalue = tag.getInt("FD");
        if (intvalue != 0 || !metFallen) {
        	instance.setFallDay(intvalue, false);
        }
        else {
            instance.setFallDay(-1, false);
        }
        instance.setPlayerInfected(tag.getBoolean("PI"));
        instance.setMetActive(tag.getBoolean("MA"));
        instance.setPrevRadius(tag.getInt("PR"));
        instance.setMetDisableLevel(tag.getInt("SC"));
        instance.setPlayerCured(tag.getBoolean("PC"));
    }
}