package dev.alef.coloroutofspace.network;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketClientPlayerData {
	
	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final CompoundNBT clientPlayerData;

    public PacketClientPlayerData(PacketBuffer buf) {
    	this.clientPlayerData = buf.readCompoundTag();
    }

    public PacketClientPlayerData(CompoundNBT tag) {
        this.clientPlayerData = tag;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeCompoundTag(this.clientPlayerData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ColorOutOfSpaceRender.updateClientPlayerData(this.clientPlayerData);
        });
        return true;
    }

}
