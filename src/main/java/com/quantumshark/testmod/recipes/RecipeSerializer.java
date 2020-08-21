package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

// todo. Make this generic? Why can't the recipe read and write itself?
// note: can pass in a constructor. 
// syntax: public interface IRecipeSerializer<T extends IRecipe<?>> 
public class RecipeSerializer<T extends IMachineRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
		implements IRecipeSerializer<T> {
	
	private IRecipeFactory<T> recipeFactory;
	public RecipeSerializer(IRecipeFactory<T> inRecipeFactory) {
		recipeFactory = inRecipeFactory;
	}
	
	public interface IRecipeFactory<T extends IMachineRecipe> {
		public T CreateRecipe(ResourceLocation recipeId);
	}

	@Override
	public T read(ResourceLocation recipeId, JsonObject json) {
		T ret = recipeFactory.CreateRecipe(recipeId);
		ret.read(json);

		return ret;
	}

	@Override
	public T read(ResourceLocation recipeId, PacketBuffer buffer) {
		T ret = recipeFactory.CreateRecipe(recipeId);
		ret.read(buffer);

		return ret;
	}

	@Override
	public void write(PacketBuffer buffer, T recipe) {
		recipe.write(buffer);

		buffer.writeItemStack(recipe.getRecipeOutput(), false);
	}
}
