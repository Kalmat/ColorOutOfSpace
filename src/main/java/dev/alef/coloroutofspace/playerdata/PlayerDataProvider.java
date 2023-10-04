package dev.alef.coloroutofspace.playerdata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerDataProvider implements ICapabilitySerializable<CompoundNBT> {
	
    @CapabilityInject(IPlayerData.class)
    public static final Capability<IPlayerData> coloroutofspaceStateCap = null;

    private LazyOptional<IPlayerData> instance = LazyOptional.of(PlayerData::new);
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap != coloroutofspaceStateCap){
            return LazyOptional.empty();
        }
        return this.instance.cast();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT)coloroutofspaceStateCap.getStorage()
                .writeNBT(coloroutofspaceStateCap,
                        instance
                            .orElseThrow(()->new IllegalArgumentException("LazyOptional must not be empty!")),
                        null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
    	coloroutofspaceStateCap.getStorage()
                .readNBT(coloroutofspaceStateCap,
                        instance
                            .orElseThrow(()->new IllegalArgumentException("LazyOptional must not be empty!")),
                        null, nbt);
    }
}