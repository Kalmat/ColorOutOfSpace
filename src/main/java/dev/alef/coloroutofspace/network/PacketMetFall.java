package dev.alef.coloroutofspace.network;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.ColorOutOfSpace;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketMetFall {
	
	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final UUID playerUUID;

    public PacketMetFall(PacketBuffer buf) {
    	this.playerUUID = buf.readUniqueId();
    }

    public PacketMetFall(UUID player, BlockPos look) {
        this.playerUUID = player;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeUniqueId(this.playerUUID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ColorOutOfSpace.forceMetFall(ctx.get().getSender().world, ctx.get().getSender());
        });
        return true;
    }

}
