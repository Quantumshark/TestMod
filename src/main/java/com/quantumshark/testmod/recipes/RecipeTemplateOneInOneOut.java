package com.quantumshark.testmod.recipes;

import net.minecraft.util.NonNullList;

public class RecipeTemplateOneInOneOut implements IRecipeTemplate {
	private final NonNullList<String> inputs = NonNullList.from(null, "input");
	private final NonNullList<String> secondaryOutputs = NonNullList.create();
	private final NonNullList<String> fluidInputs = NonNullList.create();
	private final NonNullList<String> fluidOutputs = NonNullList.create();

	@Override
	public final NonNullList<String> getInputs() {
		return inputs;
	}
	
	@Override
	public final NonNullList<String> getSecondaryOutputs() {
		return secondaryOutputs;
	}

	private RecipeTemplateOneInOneOut() {}
	
	public static final RecipeTemplateOneInOneOut INST = new RecipeTemplateOneInOneOut();

	@Override
	public NonNullList<String> getFluidInputs() {
		return fluidInputs;
	}

	@Override
	public NonNullList<String> getFluidOutputs() {
		return fluidOutputs;
	}
}
