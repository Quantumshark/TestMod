package com.quantumshark.testmod.recipes;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase;
import com.quantumshark.testmod.tileentity.MachineTileEntityBase.SlotWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MachineInventoryRecipeWrapper implements IInventory {
	public MachineInventoryRecipeWrapper(MachineTileEntityBase machine, MachineRecipeBase recipe) {
		inputSlots = machine.getInputSlots(recipe);
		outputSlots = machine.getOutputSlots(recipe);
	}

	private final MachineTileEntityBase.SlotWrapper[] inputSlots;
	private final MachineTileEntityBase.SlotWrapper[] outputSlots;

	public SlotWrapper getInputSlot(int i) {
		return inputSlots[i];
	}
	
	public SlotWrapper getOutputSlot(int i) {
		return outputSlots[i];
	}

	// this is good enough for match, but can't be updated.
	public RecipeComponent getInputWrapper(int i) {
		return inputSlots[i].getRecipeComponent();
	}

	// IInventory implementation. Not sure we need any of this.
	@Override
	public void clear() {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		machineInventory.clear();
	}

	@Override
	public int getSizeInventory() {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		return recipeTemplate.getInputs().size();
	}

	@Override
	public boolean isEmpty() {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		return false;
		/*
		 * for(int i = 0; i < inv.getSlots(); i++) {
		 * if(!inv.getStackInSlot(i).isEmpty()) return false; } return true;
		 */
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
//		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		throw new IllegalStateException("unexpected call of MachineInventoryRecipeWrapper");
	}

	// copied from RecipeWrapper:
	// The following methods are never used by vanilla in crafting. They are defunct
	// as mods need not override them.
	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void openInventory(PlayerEntity player) {
	}

	@Override
	public void closeInventory(PlayerEntity player) {
	}
}
