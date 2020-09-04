package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.capability.HeatCapabilityProvider;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CopperHeatPipeTileEntity extends TileEntity implements ICapabilityProvider, ITickableTileEntity {
	public CopperHeatPipeTileEntity() {
		super(RegistryHandler.COPPER_HEAT_PIPE_TILE_ENTITY.get());
	}
	
	protected HeatCapabilityProvider heat = new HeatCapabilityProvider(35000, 5500, 500);

	@Override
	public void tick() {
		if(heat != null) {
			heat.tick(world, pos);
		}
		BlockState bs = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, bs, bs, 2);		
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket(){
	    CompoundNBT nbtTag = new CompoundNBT();
	    //Write your data into the nbtTag
	    if(heat != null)
	    {
	    	nbtTag.put(MachineTileEntityBase.NBT_TAG_HEAT, heat.serializeNBT());
	    }
	    return new SUpdateTileEntityPacket(getPos(), -1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
	    CompoundNBT tag = pkt.getNbtCompound();
	    //Handle your Data
		if (tag != null && heat != null) {
			heat.deserializeNBT(tag);
		}
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		CompoundNBT items = compound.getCompound(MachineTileEntityBase.NBT_TAG_HEAT);
		if (items != null) {
			heat.deserializeNBT(items);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		compound.put(MachineTileEntityBase.NBT_TAG_HEAT, heat.serializeNBT());

		return compound;
	}
	
	@Override
	public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
		if (cap == RegistryHandler.CAPABILITY_HEAT && heat != null) {
			return RegistryHandler.CAPABILITY_HEAT.orEmpty(cap, LazyOptional.of(() -> heat));
		}
		return super.getCapability(cap, side);
	}
}
