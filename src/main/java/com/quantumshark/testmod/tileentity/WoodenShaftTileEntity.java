package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class WoodenShaftTileEntity extends ShaftTileEntity implements ICapabilityProvider {

	public WoodenShaftTileEntity() {
		super(RegistryHandler.WOODEN_SHAFT_TILE_ENTITY.get());
	}	
}
