package com.quantumshark.testmod.recipes;

public interface IRecipeTagMerge {
	// return -1 for none.
	abstract int getTagSource(int outputSlotIndex);
}
