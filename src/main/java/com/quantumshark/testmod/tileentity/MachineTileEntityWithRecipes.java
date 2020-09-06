package com.quantumshark.testmod.tileentity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.quantumshark.testmod.recipes.MachineInventoryRecipeWrapper;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeAndWrapper;
import com.quantumshark.testmod.recipes.RecipeComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MachineTileEntityWithRecipes extends MachineTileEntityBase {

	public MachineTileEntityWithRecipes(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		// TODO Auto-generated constructor stub
	}

	protected abstract NonNullList<IRecipeType<MachineRecipeBase>> getRecipeTypes();
	protected abstract MachineInventoryRecipeWrapper getInventoryWrapperForRecipe(MachineRecipeBase recipe);
	public abstract SlotWrapper[] getInputSlots(MachineRecipeBase recipe);
	public abstract SlotWrapper[] getCatalystSlots(MachineRecipeBase recipe);
	public abstract SlotWrapper[] getOutputSlots(MachineRecipeBase recipe);

	// note: for single type at least, cache this?
	private Set<MachineRecipeBase> findRecipes() {
		if (world == null) {
			return Collections.emptySet();
		}
		return world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> getRecipeTypes().contains(recipe.getType())).map(x -> (MachineRecipeBase) (x))
				.collect(Collectors.toSet());
	}

	@Nullable
	public RecipeAndWrapper findMatchingRecipe() {
		Set<MachineRecipeBase> recipes = findRecipes();
		for (MachineRecipeBase recipe : recipes) {
			MachineInventoryRecipeWrapper wrapper = getInventoryWrapperForRecipe(recipe);
			// note: this is going to be a bit tricky performance-wise in the generic
			// scenario, as we'd have to get a wrapper for each recipe in turn
			// single type version is a lot easier
			if (recipe.matches(wrapper, this.world)) {
				return new RecipeAndWrapper(recipe, wrapper);
			}
		}

		return null;
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}
	
	// this is only used for items, hence the ItemStack signature.
	// can shift-click to insert into either an input or a catalyst slot
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getInputSlotCount() + getCatalystSlotCount()) {
			// don't allow insertion into output slots.
			return false;
		}
		RecipeComponent inputWrapper = RecipeComponent.wrap(stack, "");

		Set<MachineRecipeBase> recipes = findRecipes();

		for (MachineRecipeBase recipe : recipes) {
			if(CheckAgainstRecipe(slot, inputWrapper, recipe, getInputSlots(recipe)))
			{
				return true;
			}
			if(CheckAgainstRecipe(slot, inputWrapper, recipe, getCatalystSlots(recipe)))
			{
				return true;
			}
		}
		return false;
	}

	private boolean CheckAgainstRecipe(int slot, RecipeComponent inputWrapper, MachineRecipeBase recipe,
			SlotWrapper[] slotsForRecipe) {
		for (int i = 0; i < slotsForRecipe.length; ++i) {
			SlotWrapper sw = slotsForRecipe[i];

			if (sw == null || !(sw instanceof SlotWrapperItem)) {
				continue;
			}

			SlotWrapperItem cast = (SlotWrapperItem) sw;
			if (cast.inventoryIndex != slot) {
				continue;
			}

			// so input i in the recipe goes in machine slot. Now just see if this matches
			// it.
			RecipeComponent ingredient = recipe.getInputs().get(i);

			if (ingredient.isFulfilledBy(inputWrapper, false)) {
				return true;
			}
		}
		return false;
	}
}
