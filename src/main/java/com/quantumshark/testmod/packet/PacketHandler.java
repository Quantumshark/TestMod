package com.quantumshark.testmod.packet;

import com.quantumshark.testmod.TestMod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";	// todo: make this match mod version?
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(TestMod.MOD_ID, "msg"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);

	private static int nextId = 0;
	public static int MSG_BLOCK_UI_ACTION;
	
	public static void init() {
		INSTANCE.registerMessage(MSG_BLOCK_UI_ACTION = nextId++, BlockUpdateMessage.class, BlockUpdateMessage::encode, BlockUpdateMessage::decode,BlockUpdateMessage::handle);
	}
	
	// https://mcforge.readthedocs.io/en/latest/networking/simpleimpl/
	// ultra sparse docs!!!!!!!!!!

	// INSTANCE.sendToServer(new MyMessage())
	
	// in packet handler
//	public static void handle(MyMessage msg, Supplier<NetworkEvent.Context> ctx) {
//	    ctx.get().enqueueWork(() -> {
//	        // Work that needs to be threadsafe (most work)
//	        EntityPlayerMP sender = ctx.get().getSender(); // the client that sent this packet
//	        // do stuff
//	    });
//	    ctx.get().setPacketHandled(true);
	//}
	
	// Sending to one player
//	INSTANCE.send(PacketDistributor.PLAYER.with(playerMP), new MyMessage());

	// Send to all players tracking this chunk
//	INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(chunk), new MyMessage());

	// Sending to all connected players
//	INSTANCE.send(PacketDistributor.ALL.noArg(), new MyMessage());	
}
