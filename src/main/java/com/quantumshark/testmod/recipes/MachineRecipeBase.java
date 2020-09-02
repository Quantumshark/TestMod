package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class MachineRecipeBase implements IMachineRecipe {
	private final ResourceLocation id;
	private NonNullList<RecipeComponent> inputs;
	private NonNullList<RecipeComponent> outputs;

	abstract RecipeTemplate getRecipeTemplate();

	private final ItemStack firstItemOutput;

	public NonNullList<RecipeComponent> getInputs() {
		return inputs;
	}

	public NonNullList<RecipeComponent> getOutputs() {
		return outputs;
	}

	public MachineRecipeBase(ResourceLocation id) {
		this.id = id;

		RecipeTemplate rt = getRecipeTemplate();
		inputs = rt.createInputs();
		outputs = rt.createOutputs();
		ItemStack firstItemOutput = null;
		for (RecipeComponent output : outputs) {
			firstItemOutput = output.getAsItemStack();
			if (firstItemOutput != null) {
				break;
			}
		}
		if (firstItemOutput == null) {
			firstItemOutput = ItemStack.EMPTY;
		}
		this.firstItemOutput = firstItemOutput;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	// actual result. We can't do this as it might not return an item?
	// RecipeWrapper is a template, so can derive our own more sophisticated
	// IInventory and pass that in, then we can actually do this.
	// it could just be the machine inventory, or could be an IInventory-compatable
	// adaptor
	@Override
	public ItemStack getCraftingResult(MachineInventoryRecipeWrapper inv) { // RecipeWrapper just needs to be a
																			// IInventory, but that's still item based
		return this.firstItemOutput;
	}

	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe
	 * book). If your recipe has more than one possible result (e.g. it's dynamic
	 * and depends on its inputs), then return an empty stack.
	 */
	@Override
	public ItemStack getRecipeOutput() {
		// todo: if first output is an item, can return that.
		return ItemStack.EMPTY;
	}

	@Override
	// just return a list that matches inputs to handle any buckets passed in.
	public NonNullList<ItemStack> getRemainingItems(MachineInventoryRecipeWrapper inv) {
		// note: we actually check the recipe for buckety things rather than the
		// inventory, but that should work right?
		int ingredientCount = inputs.size();
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(ingredientCount, ItemStack.EMPTY);

		for (int i = 0; i < ingredientCount; ++i) {
			ItemStack item = inputs.get(i).getAsItemStack();
			if (item == null) {
				continue;
			}
			if (!item.hasContainerItem()) {
				continue;
			}
			nonnulllist.set(i, item.getContainerItem());
		}

		return nonnulllist;
	}

	@Override
	public boolean matches(MachineInventoryRecipeWrapper inv, World worldIn) {
		// note: do this for each named input slot. This is what maps the slots (e.g.,
		// "input") to the inventories (sequential starting with 0)
		for (int i = 0; i < getRecipeTemplate().getInputs().size(); ++i) {
			if (!this.inputs.get(i).isFulfilledBy(inv.getInputWrapper(i), true)) {
				return false;
			}
		}
		return true;
	}

	// if the recipe type has one or more secondary output slots ... panic

	@Override
	public NonNullList<Ingredient> getIngredients() {
		throw new IllegalStateException("unexpected call of MachineRecipeBase");
		// add each input slot to this line, with commas in between
		// throw if called?
//		return null;
	}

	@Override
	public void read(JsonObject json) {
		for (RecipeComponent input : inputs) {
			input.read(json);
		}

		for (RecipeComponent output : outputs) {
			output.read(json);
		}
	}

	@Override
	public void read(PacketBuffer buffer) {
		for (RecipeComponent input : inputs) {
			input.read(buffer);
		}

		for (RecipeComponent output : outputs) {
			output.read(buffer);
		}
	}

	@Override
	public void write(PacketBuffer buffer) {
		for (RecipeComponent input : inputs) {
			input.write(buffer);
		}

		for (RecipeComponent output : outputs) {
			output.write(buffer);
		}
	}
}
