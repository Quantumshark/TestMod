package com.quantumshark.testmod.recipes;

import net.minecraft.util.NonNullList;

public class RecipeTemplateGrinder implements IRecipeTemplate {
	private final NonNullList<String> inputs = NonNullList.from(null, "input");
	private final NonNullList<String> secondaryOutputs = NonNullList.create();
	private final NonNullList<String> fluidInputs = NonNullList.create();
	private final NonNullList<String> fluidOutputs = NonNullList.from(null, "fluid_output");

	@Override
	public final NonNullList<String> getInputs() {
		return inputs;
	}
	
	@Override
	public final NonNullList<String> getSecondaryOutputs() {
		return secondaryOutputs;
	}

	private RecipeTemplateGrinder() {}
	
	public static final RecipeTemplateGrinder INST = new RecipeTemplateGrinder();

	@Override
	public NonNullList<String> getFluidInputs() {
		return fluidInputs;
	}

	@Override
	public NonNullList<String> getFluidOutputs() {
		return fluidOutputs;
	}
}
