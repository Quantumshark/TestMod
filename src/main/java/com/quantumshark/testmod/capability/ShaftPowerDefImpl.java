package com.quantumshark.testmod.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ShaftPowerDefImpl implements IShaftPower, INBTSerializable<CompoundNBT> {

	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
	}
}
