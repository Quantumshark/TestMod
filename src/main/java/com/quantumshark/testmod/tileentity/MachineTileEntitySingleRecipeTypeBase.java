package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.recipes.MachineInventoryRecipeWrapper;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeTemplate;
import com.quantumshark.testmod.recipes.RecipeTemplateComponent;
import com.quantumshark.testmod.utill.MachineFluidHandler;
import com.quantumshark.testmod.utill.MachineItemHandler;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

public abstract class MachineTileEntitySingleRecipeTypeBase extends MachineTileEntityBase {
	public MachineTileEntitySingleRecipeTypeBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
		inputSlots = new SlotWrapper[getRecipeTemplate().getInputs().size()];
		
		for(int i=0;i<inputSlots.length;++i) {
			RecipeTemplateComponent rtc = getRecipeTemplate().getInputs().get(i);
			switch(rtc.getComponentType() ) {
				case Item:
					inputSlots[i] = new SlotWrapperItem(getInputSlotCount(), rtc.getName());
					inputSlotCount = getInputSlotCount() + 1;
					break;
				case Fluid:
					inputSlots[i] = new SlotWrapperFluid(inputFluidSlotCount, rtc.getName());
					++inputFluidSlotCount;
					break;
				default:
					// panic!
					break;
			}
		}
		
		outputSlots = new SlotWrapper[getRecipeTemplate().getOutputs().size()];
		
		for(int i=0;i<outputSlots.length;++i) {
			RecipeTemplateComponent rtc = getRecipeTemplate().getOutputs().get(i);
			switch(rtc.getComponentType() ) {
				case Item:
					outputSlots[i] = new SlotWrapperItem(outputSlotCount, rtc.getName());
					++outputSlotCount;
					break;
				case Fluid:
					outputSlots[i] = new SlotWrapperFluid(outputFluidSlotCount, rtc.getName());
					++outputFluidSlotCount;
					break;
				default:
					// panic!
					break;
			}
		}			
		
		inputSlotCount = getInputSlotCount() + getNonRecipeInputSlotCount();
		outputSlotCount += getNonRecipeOutputSlotCount();
		
		inventory = new MachineItemHandler(getInputSlotCount() + outputSlotCount, this);
		fluidInventory = new MachineFluidHandler(inputFluidSlotCount + outputFluidSlotCount);
	}
	
	private final SlotWrapper[] inputSlots;
	private final SlotWrapper[] outputSlots;
	
	private final NonNullList<IRecipeType<MachineRecipeBase>> recipeTypes = NonNullList.from(null, getRecipeType());

	protected abstract IRecipeType<MachineRecipeBase> getRecipeType();
	
	@Override
	protected NonNullList<IRecipeType<MachineRecipeBase>> getRecipeTypes() {
		return recipeTypes;		
	}
	
	@Override
	protected MachineInventoryRecipeWrapper getInventoryWrapperForRecipe(MachineRecipeBase recipe) {
		// note: null here is fine because it's only ever actually used in the tile entity's getInputSlots(recipe)
		// and ours doesn't look at recipe.
		// however the fact that we create the wrapper which then calls back into us leaves a bit of a bad smell
		return new MachineInventoryRecipeWrapper(this, recipe);
	}
	
	@Override
	public SlotWrapper[] getInputSlots(MachineRecipeBase recipe) {
		return inputSlots;
	}
	
	@Override
	public SlotWrapper[] getOutputSlots(MachineRecipeBase recipe) {
		return outputSlots;
	}
	
	// move to a single type child class
	protected abstract RecipeTemplate getRecipeTemplate();
	
	// overrideable in concrete classes
	protected int getNonRecipeInputSlotCount() {
		return 0;
	}

	protected int getNonRecipeOutputSlotCount() {
		return 0;
	}
}
