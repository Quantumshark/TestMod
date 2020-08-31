package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.capability.ShaftPowerDefImpl;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ShaftTileEntity extends NetworkTileEntity implements ICapabilityProvider {
	private ShaftPowerDefImpl shaft;

	public ShaftTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
		shaft = new ShaftPowerDefImpl();
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
