package dev.alef.coloroutofspace.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.alef.coloroutofspace.Refs;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

	@SuppressWarnings("unused")
    private final static Logger LOGGER = LogManager.getLogger();
	
	private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
    	
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Refs.MODID, "coloroutofspace"),
                () -> "1.0",
                s -> true,
                s -> true);

        INSTANCE.messageBuilder(PacketInfected.class, nextID())
			.encoder(PacketInfected::toBytes)
			.decoder(PacketInfected::new)
			.consumer(PacketInfected::handle)
			.add();
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
    	INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
    
    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
