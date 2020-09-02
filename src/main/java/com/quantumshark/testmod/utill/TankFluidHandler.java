package com.quantumshark.testmod.utill;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TankFluidHandler extends FluidTank implements IFluidHandler {

	public TankFluidHandler(int capacity) {
		super(capacity);
	}
}
