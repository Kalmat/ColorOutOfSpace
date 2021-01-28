package dev.alef.coloroutofspace.network;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketInfected {
	
	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private final boolean infected;
	private final int cureLevel;
	private final boolean cured;

    public PacketInfected(PacketBuffer buf) {
    	this.infected = buf.readBoolean();
    	this.cureLevel = buf.readInt();
    	this.cured = buf.readBoolean();
    }

    public PacketInfected(boolean isInfected, int level, boolean justCured) {
        this.infected = isInfected;
        this.cureLevel = level;
        this.cured = justCured;
    }

    public void toBytes(PacketBuffer buf) {
    	buf.writeBoolean(this.infected);
    	buf.writeInt(this.cureLevel);
    	buf.writeBoolean(this.cured);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ColorOutOfSpaceRender.setPlayerInfected(this.infected, this.cureLevel, this.cured);
        });
        return true;
    }

}
