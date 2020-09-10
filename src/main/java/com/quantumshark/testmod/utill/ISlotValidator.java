package com.quantumshark.testmod.utill;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ISlotValidator {
	public default boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return false;
	}

	public default boolean isFluidValid(int slot, @Nonnull FluidStack stack) {
		return false;
	}
}
