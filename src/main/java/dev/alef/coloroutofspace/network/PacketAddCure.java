package dev.alef.coloroutofspace.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;

public class PacketAddCure {

	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final UUID player;
    private final int cureLevel;

    public PacketAddCure(PacketBuffer buf) {
    	player = buf.readUniqueId();
    	cureLevel = buf.readInt();
    }

    public PacketAddCure(UUID player, int cureLevel) {
        this.player = player;
        this.cureLevel = cureLevel;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeUniqueId(player);
    	buf.writeInt(cureLevel);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ColorOutOfSpaceRender.setCureLevel(this.cureLevel);
        });
        return true;
    }
}
