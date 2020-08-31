package com.quantumshark.testmod.recipes;

import javax.annotation.Nonnull;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.utill.RecipeInit;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

// in <> put the template class name
public class GrinderRecipe extends MachineRecipeBase<RecipeTemplateGrinder> {
	
	// return a new recipe of this type - used for reading recipes.
	public static class RecipeFactory implements RecipeSerializer.IRecipeFactory<GrinderRecipe> {

		@Override
		public GrinderRecipe CreateRecipe(ResourceLocation recipeId) {
			return new GrinderRecipe(recipeId);
		}
	}

	// constructor, so just match the class name
	public GrinderRecipe(ResourceLocation id) {
		super(id);
	}

	// the recipe template (combination of inputs and secondary outputs if any). Just update the template class name.
	@Override
	public RecipeTemplateGrinder getRecipeTemplate() {
		return RecipeTemplateGrinder.INST;
	}
	
	// return the recipe type id for this type of recipe
	public static ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(TestMod.MOD_ID, "grinder");

	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return Registry.RECIPE_TYPE.getValue(RECIPE_TYPE_ID).get();
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		// return recipe serializer (the thing that knows how to load this kind of
		// recipe from a json file)
		// need to create this in registry for each recipe type
		return RecipeInit.GRINDER_RECIPE_SERIALIZER.get();
	}	
	
	@Override
	public ItemStack getIcon() {
		// this is the icon that appears next to the recipe type - use the machine this
		// recipe type is for (the simplest one if many ...)
		return new ItemStack(RegistryHandler.GRINDER_BLOCK.get());
	}	
}
