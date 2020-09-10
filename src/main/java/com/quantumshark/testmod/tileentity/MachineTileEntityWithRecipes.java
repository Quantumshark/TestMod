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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

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
			if(CheckAgainstRecipe(slot, inputWrapper, recipe.getInputs(), getInputSlots(recipe)))
			{
				return true;
			}
			if(CheckAgainstRecipe(slot, inputWrapper, recipe.getCatalysts(), getCatalystSlots(recipe)))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isFluidValid(int slot, FluidStack stack) {
		if (slot < 0 || slot >= getInputSlotCount() + getCatalystSlotCount()) {
			// don't allow insertion into output slots.
			return false;
		}
		RecipeComponent inputWrapper = RecipeComponent.wrap(stack, "");

		Set<MachineRecipeBase> recipes = findRecipes();

		for (MachineRecipeBase recipe : recipes) {
			if(CheckAgainstRecipe(slot, inputWrapper, recipe.getInputs(), getInputSlots(recipe)))
			{
				return true;
			}
			if(CheckAgainstRecipe(slot, inputWrapper, recipe.getCatalysts(), getCatalystSlots(recipe)))
			{
				return true;
			}
		}
		return false;
	}
	
	protected boolean isItemValidForFluidSlot(int slot, int tankSlot, ItemStack stack, boolean allowFill, boolean allowEmpty) {
		IFluidHandlerItem h = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				.orElse(null);
		if (h != null) {
			FluidStack fluid = h.getFluidInTank(0);
			if(fluid != null)
			{
				if(allowFill)
				{
					if(isFluidValid(tankSlot, fluid))
					{
						return true;
					}
				}
				if(allowEmpty) {
					FluidStack fluidInTank = fluidInventory.getFluidInTank(tankSlot);
					if((h.getTankCapacity(0) > fluid.getAmount()) && (fluid.isEmpty() || fluidInTank.isEmpty()
							|| fluid.isFluidEqual(fluidInTank))) {
						return true;
					}
				}
			}
		}
//		if (stack.getItem() == Items.BUCKET
//				|| (stack.getContainerItem() != null && stack.getContainerItem().getItem() == Items.BUCKET)) {
//			return true;
//		}
		return false;		
	}

	// offset is set for catalyst slots to be the number of input slots in the recipe
	// so we start comparing catalyst v catalyst
	private boolean CheckAgainstRecipe(int slot, RecipeComponent inputWrapper, NonNullList<RecipeComponent> recipeComponents,
			SlotWrapper[] slotsForRecipe) {
		for (int i = 0; i < slotsForRecipe.length; ++i) {
			SlotWrapper sw = slotsForRecipe[i];

			if (sw == null) {
				continue;
			}

			// this needs to handle different recipe component types.
			// this will mean we check against both item and fluid slots with this index ...
			if (sw.inventoryIndex != slot) {
				continue;
			}

			// so input i in the recipe goes in machine slot. Now just see if this matches
			// it.
			RecipeComponent ingredient = recipeComponents.get(i);

			// note: this checks type, so if we're in the wrong type, it will return false, which is good.
			// odd, but it works ...
			if (ingredient.isFulfilledBy(inputWrapper, false)) {
				return true;
			}
		}
		return false;
	}
}
