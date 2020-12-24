package dev.alef.coloroutofspace.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketInfected {
	
	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final UUID player;

    public PacketInfected(PacketBuffer buf) {
    	player = buf.readUniqueId();
    }

    public PacketInfected(UUID playerUUID) {
        this.player = playerUUID;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeUniqueId(player);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ColorOutOfSpaceRender.setPlayerInfected(true);
        });
        return true;
    }

}
