package com.quantumshark.testmod.utill;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface ISlotValidator {
	public boolean isItemValid(int slot, @Nonnull ItemStack stack);
}
