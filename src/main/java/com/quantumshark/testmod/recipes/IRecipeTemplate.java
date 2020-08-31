package com.quantumshark.testmod.recipes;

import net.minecraft.util.NonNullList;

// template for a recipe type.
// recipe classes and machine classes have to use the same template (at least for now).
public interface IRecipeTemplate {
	NonNullList<String> getInputs();
	public NonNullList<String> getSecondaryOutputs();
	
	public NonNullList<String> getFluidInputs();
	public NonNullList<String> getFluidOutputs();
}
