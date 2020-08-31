package com.quantumshark.testmod.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ShaftPowerStorage implements IStorage<IShaftPower> {

	@Override
	public INBT writeNBT(Capability<IShaftPower> capability, IShaftPower instance, Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readNBT(Capability<IShaftPower> capability, IShaftPower instance, Direction side, INBT nbt) {
		// TODO Auto-generated method stub
	}
}
