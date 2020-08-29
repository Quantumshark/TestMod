package com.quantumshark.testmod.recipes;

import net.minecraft.util.NonNullList;

// template for a recipe type.
// recipe classes and machine classes have to use the same template (at least for now).
public class RecipeTemplate {
	private final NonNullList<String> inputs;
	private final NonNullList<String> secondaryOutputs;
	
	public final NonNullList<String> getInputs() {
		return inputs;
	}
	
	public final NonNullList<String> getSecondaryOutputs() {
		return secondaryOutputs;
	}
	
	public RecipeTemplate(NonNullList<String> inputs) {
		this(inputs, NonNullList.create());
	}
	
	public RecipeTemplate(NonNullList<String> inputs, NonNullList<String> secondaryOutputs)
	{
		this.inputs = inputs;
		this.secondaryOutputs = secondaryOutputs;
	}
	
	// define any templates statically so we can validate with ==
	public static final RecipeTemplate ONE_IN_ONE_OUT = new RecipeTemplate(NonNullList.from(null, "input"));
}
