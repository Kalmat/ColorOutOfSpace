package dev.alef.coloroutofspace.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;

public class PacketCured {

	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final UUID player;

    public PacketCured(PacketBuffer buf) {
    	player = buf.readUniqueId();
    }

    public PacketCured(UUID player) {
        this.player = player;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeUniqueId(player);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ColorOutOfSpaceRender.setPlayerInfected(false);
        });
        return true;
    }
}
