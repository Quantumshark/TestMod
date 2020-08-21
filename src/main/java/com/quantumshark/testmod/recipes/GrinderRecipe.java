package com.quantumshark.testmod.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.utill.RecipeInit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

// todo: can some of this be put into a common base class?
// different recipe types with the same pattern of inputs / outputs could share a recipe class too
public class GrinderRecipe implements IMachineRecipe {
	
	public static class RecipeFactory implements RecipeSerializer.IRecipeFactory<GrinderRecipe> {

		@Override
		public GrinderRecipe CreateRecipe(ResourceLocation recipeId) {
			return new GrinderRecipe(recipeId);
		}
	}

	public static ResourceLocation RECIPE_TYPE_ID = new ResourceLocation(TestMod.MOD_ID, "grinder");
	
	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return Registry.RECIPE_TYPE.getValue(RECIPE_TYPE_ID).get();
	}

	private final ResourceLocation id;
	private Ingredient input;
	private ItemStack output;

	public GrinderRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(RecipeWrapper inv, World worldIn) {
		if (this.input.test(inv.getStackInSlot(0))) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(RecipeWrapper inv) {
		return this.output;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.output;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeInit.GRINDER_RECIPE_SERIALIZER.get();
	}

//	@Override
	public Ingredient getInput() {
		return this.input;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.from(null, this.input);
	}
	
	@Override
	public void read(JsonObject json) {
		ItemStack output = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "output"), true);
		Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
		this.output = output;
		this.input = input;
	}

	@Override
	public void read(PacketBuffer buffer) {
		ItemStack output = buffer.readItemStack();
		Ingredient input = Ingredient.read(buffer);
		this.output = output;
		this.input = input;
	}

	@Override
	public void write(PacketBuffer buffer) {
		Ingredient input = getIngredients().get(0);
		input.write(buffer);

		buffer.writeItemStack(getRecipeOutput(), false);
	}	
}
