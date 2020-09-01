package com.quantumshark.testmod.recipes;

import com.quantumshark.testmod.recipes.RecipeTemplate.ComponentType;

public class RecipeTemplateComponent {
	private String name;
	private ComponentType componentType;

	public String getName() {
		return name;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public RecipeTemplateComponent(String inStr) {
		String[] bits = inStr.split(":");
		name = bits[0];
		if (bits.length > 1) {
			componentType = ComponentType.valueOf(bits[1]);
		} else {
			componentType = ComponentType.Item; // default
		}
	}

	public RecipeComponent instantiate() {
		return RecipeComponent.create(componentType, name);
	}
}
