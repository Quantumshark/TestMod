package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public abstract class MachineRecipeBase<T extends IRecipeTemplate> implements IMachineRecipe {
	private final ResourceLocation id;
	private ItemStack output;
	
	abstract T getRecipeTemplate();
	
	public MachineRecipeBase(ResourceLocation id) {
		this.id = id;
		
		T rt = getRecipeTemplate();
		inputs = NonNullList.withSize(rt.getInputs().size(), Ingredient.EMPTY);
		secondaryOutputs = NonNullList.withSize(rt.getSecondaryOutputs().size(), ItemStack.EMPTY);
		fluidOutputs = NonNullList.withSize(rt.getFluidOutputs().size(), FluidStack.EMPTY);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
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
	// this is where any secondary outputs are returned.
	// the default implementation (copied, nearly) returns an empty bucket for each input that is a bucket of something (ish)
	// and, actually, each output ...
	public NonNullList<ItemStack> getRemainingItems(RecipeWrapper inv) {

		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		
		int ingredientCount = getIngredients().size();

		for (int i = 0; i < ingredientCount; ++i) {
			ItemStack item = inv.getStackInSlot(i);
			if (item.hasContainerItem()) {
				nonnulllist.set(i, item.getContainerItem());
			}
		}
		
		// todo: ingredientCount+2 onwards are potential secondaries.
		// these ought to have an ItemStack plus some kind of probability function that includes a maximum
		// though it would default to "always exactly one"
		
		return nonnulllist;
	}
	
	// *** stuff that can be shared by recipe types with the same pattern of inputs and outputs ***

	// define an Ingredient for each distinct input slot (inventories in machine)
	// note: can definitely use FluidStack here ... not sure beyond that
	private NonNullList<Ingredient> inputs;
	private NonNullList<ItemStack> secondaryOutputs;
	private NonNullList<FluidStack> fluidOutputs;

	@Override
	public boolean matches(RecipeWrapper inv, World worldIn) {
		// note: do this for each named input slot. This is what maps the slots (e.g.,
		// "input") to the inventories (sequential starting with 0)
		for(int i=0;i<getRecipeTemplate().getInputs().size();++i)
		{
			if (!this.inputs.get(i).test(inv.getStackInSlot(i))) {
				return false;
			}
		}
		return true;
	}

	// if the recipe type has one or more secondary output slots ... panic

	@Override
	public NonNullList<Ingredient> getIngredients() {
		// add each input slot to this line, with commas in between
		return inputs;
	}

	@Override
	public void read(JsonObject json) {
		ItemStack temp;
		output = RecipeSerializer.getItemStack(json, "output", true);
	
		T rt = getRecipeTemplate();
		// copy this for each input slot
		for(int i=0;i<rt.getInputs().size();++i)
		{
			String name = rt.getInputs().get(i);
			inputs.set(i, Ingredient.deserialize(JSONUtils.getJsonObject(json, name)));
		}
		
		for(int i=0;i<rt.getSecondaryOutputs().size();++i)
		{
			String name = rt.getSecondaryOutputs().get(i);
			secondaryOutputs.set(i, RecipeSerializer.getItemStack(json, name, true));
		}

		for(int i=0;i<rt.getFluidOutputs().size();++i)
		{
			String name = rt.getFluidOutputs().get(i);
			fluidOutputs.set(i, RecipeSerializer.getFluidStack(json, name, true));
		}
	}

	@Override
	public void read(PacketBuffer buffer) {
		T rt = getRecipeTemplate();
		for(int i=0;i<rt.getInputs().size();++i)
		{
			inputs.set(i, Ingredient.read(buffer));
		}

		output = buffer.readItemStack();

		for(int i=0;i<rt.getSecondaryOutputs().size();++i)
		{
			secondaryOutputs.set(i, buffer.readItemStack());
		}

		for(int i=0;i<rt.getFluidOutputs().size();++i)
		{
			fluidOutputs.set(i, buffer.readFluidStack());
		}		
	}

	
	@Override
	public void write(PacketBuffer buffer) {
		T rt = getRecipeTemplate();
		for(int i=0;i<rt.getInputs().size();++i)
		{
			inputs.get(i).write(buffer);
		}

		buffer.writeItemStack(output, false);

		for(int i=0;i<rt.getSecondaryOutputs().size();++i)
		{
			buffer.writeItemStack(secondaryOutputs.get(i), false);
		}
		
		for(int i=0;i<rt.getFluidOutputs().size();++i)
		{
			buffer.writeFluidStack(fluidOutputs.get(i));
		}		
	}
}
