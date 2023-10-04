package dev.alef.coloroutofspace.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketMetHarvested {
	
	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final UUID playerUUID;

    public PacketMetHarvested(PacketBuffer buf) {
    	this.playerUUID = buf.readUniqueId();
    }

    public PacketMetHarvested(UUID player) {
        this.playerUUID = player;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeUniqueId(this.playerUUID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ColorOutOfSpaceRender.setMetHarvested(true);
        });
        return true;
    }

}
