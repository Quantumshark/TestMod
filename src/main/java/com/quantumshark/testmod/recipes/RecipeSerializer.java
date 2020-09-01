package com.quantumshark.testmod.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

// todo. Make this generic? Why can't the recipe read and write itself?
// note: can pass in a constructor. 
// syntax: public interface IRecipeSerializer<T extends IRecipe<?>> 
public class RecipeSerializer<T extends IMachineRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
		implements IRecipeSerializer<T> {

	private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
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

	// return empty stack rather than crashing if not found
	public static ItemStack getItemStack(JsonObject json, String memberName, boolean readNBT) {
		if (!json.has(memberName)) {
			return ItemStack.EMPTY;
		}

		return CraftingHelper.getItemStack(JSONUtils.getJsonObject(json.get(memberName), memberName), readNBT);
	}

	public static FluidStack getFluidStack(JsonObject json, String memberName, boolean readNBT) {
		if (!json.has(memberName)) {
			return FluidStack.EMPTY;
		}
		json = JSONUtils.getJsonObject(json.get(memberName), memberName);
		String fluidName = JSONUtils.getString(json, "fluid");

		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));

		if (fluid == null)
			throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");

		if (readNBT && json.has("nbt")) {
			// Lets hope this works? Needs test
			try {
				JsonElement element = json.get("nbt");
				CompoundNBT nbt;
				if (element.isJsonObject())
					nbt = JsonToNBT.getTagFromJson(GSON.toJson(element));
				else
					nbt = JsonToNBT.getTagFromJson(JSONUtils.getString(element, "nbt"));

				CompoundNBT tmp = new CompoundNBT();

				tmp.put("Tag", nbt);
				tmp.putString("FluidName", fluidName);
				tmp.putInt("Amount", JSONUtils.getInt(json, "amount", 1000));

				return FluidStack.loadFluidStackFromNBT(tmp);
			} catch (CommandSyntaxException e) {
				throw new JsonSyntaxException("Invalid NBT Entry: " + e.toString());
			}
		}

		return new FluidStack(fluid, JSONUtils.getInt(json, "amount", 1000));
	}

}
