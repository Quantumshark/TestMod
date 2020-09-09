package com.quantumshark.testmod.packet;

import java.util.function.Supplier;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BlockUpdateMessage {
	private World world;	// actually dimension
	private BlockPos pos;
	private CompoundNBT body;
	
	public BlockUpdateMessage(World world, BlockPos pos, CompoundNBT body) {
		this.world = world;
		this.pos = pos;
		this.body = body;
	}
	public void encode(PacketBuffer buf) {
		// is this correct?
		buf.writeInt(world.getDimension().getType().getId());
		buf.writeBlockPos(pos);
		buf.writeCompoundTag(body);
	}
	
	public static BlockUpdateMessage decode(PacketBuffer buf) {
		int dimensionId = buf.readInt();
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		DimensionType dt = DimensionType.getById(dimensionId);
		World world = DimensionManager.getWorld(server, dt, true, true);		
		BlockPos pos = buf.readBlockPos();
		CompoundNBT body = buf.readCompoundTag();
		return new BlockUpdateMessage(world, pos, body);
	}
	
	public void handle(Supplier<NetworkEvent.Context>ctx  ) {
        ctx.get().enqueueWork(() -> {
        	// send message to machine tile entity at pos
        	TileEntity t = world.getTileEntity(pos);
        	if(t != null && t instanceof MachineTileEntityBase)
        	{
        		MachineTileEntityBase cast = (MachineTileEntityBase) t;
        		cast.onCustomDataPacket(body);
        	}
        });
        ctx.get().setPacketHandled(true);		
	}
}
