package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.capability.ShaftPowerDefImpl;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ShaftTileEntity extends TileEntity implements ICapabilityProvider {
	private ShaftPowerDefImpl shaft;

	public ShaftTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
		shaft = new ShaftPowerDefImpl();
	}

	// todo: this is half generic and half not. Make specific classes for each to handle this part.
	public ShaftTileEntity() {
		this(RegistryHandler.WOODEN_SHAFT_TILE_ENTITY.get());
	}	
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == RegistryHandler.CAPABILITY_SHAFT_POWER)
		{
			return RegistryHandler.CAPABILITY_SHAFT_POWER.orEmpty(cap, LazyOptional.of(() -> this.shaft));
		}
		return super.getCapability(cap, side);
	}	
}
