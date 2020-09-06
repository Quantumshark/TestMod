package com.quantumshark.testmod.utill;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class MachineItemCapabilityHandler implements IItemHandler {
	public MachineItemCapabilityHandler(IItemHandler inventory, int noExtractSlotCount) {
		this.inventory = inventory;
		this.noExtractSlotCount = noExtractSlotCount;
	}

	private final IItemHandler inventory;
	private final int noExtractSlotCount;

	@Override
	public int getSlots() {
		return inventory.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return inventory.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot < noExtractSlotCount) {
			return ItemStack.EMPTY;
		}
		return inventory.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inventory.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValid(slot, stack);
	}
}
