package com.quantumshark.testmod.recipes;

import net.minecraft.util.NonNullList;

// template for a recipe type.
// recipe classes and machine classes have to use the same template (at least for now).
public class RecipeTemplate {
	// todo: composite ingredient and output classes
	// move the load / save / serialize / deserealise code here
	// integrate with inventory holder types to get the inputs and outputs
	// upgrade MachineItemHandler to wrap heterogeneous inventories
	// also add the capability to insert a container (for types that have them) for
	// input or output slots
	// all inputs share a common return slot?
	
	// note: machine inventory is more complex. It starts very like recipe (a bunch of wrapped, generic stacky things)
	// but it can include additional inventory slots for buckets (e.g.).
	// it needs to support an interface that numbers slots of each type sequentially, e.g., item slots 0-4, fluid slots 0-3, ...
	// is that a series of adapters? 
	// and is there an adapter for the recipe as well?
	// make it have heterogeneous lists and have a mapping that returns a RecipeComponent wrapper of the right type based on recipe slot number
	
	private NonNullList<RecipeTemplateComponent> inputs;
	private NonNullList<RecipeTemplateComponent> outputs;
	
	public NonNullList<RecipeTemplateComponent> getInputs() {
		return inputs;
	}
	public NonNullList<RecipeTemplateComponent> getOutputs() {
		return outputs;
	}
	
	public enum ComponentType {
		Item, Fluid, None;
	}

	private RecipeTemplate(String strInputs, String strOutputs) {
		String[] bits = strInputs.split(",");
		inputs = NonNullList.create();
		for(String s: bits)
		{
			if(s.length() > 0)
			{
				inputs.add(new RecipeTemplateComponent(s));
			}
		}

		bits = strOutputs.split(",");
		outputs = NonNullList.create();
		for(String s: bits)
		{
			if(s.length() > 0)
			{
				outputs.add(new RecipeTemplateComponent(s));
			}
		}
	}
	
	public NonNullList<RecipeComponent> createInputs() {
		NonNullList<RecipeComponent> ret = NonNullList.create();
		for(RecipeTemplateComponent c: inputs) {
			ret.add(c.instantiate());
		}
		return ret;
	}

	public NonNullList<RecipeComponent> createOutputs() {
		NonNullList<RecipeComponent> ret = NonNullList.create();
		for(RecipeTemplateComponent c: outputs) {
			ret.add(c.instantiate());
		}
		return ret;
	}
	
	public static final RecipeTemplate GRINDER = new RecipeTemplate("input", "output:Item,juice:Fluid");
	public static final RecipeTemplate ITEM_IN_ITEM_OUT = new RecipeTemplate("input", "output");
}
