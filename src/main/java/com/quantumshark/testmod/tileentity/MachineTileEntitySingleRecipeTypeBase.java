package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.recipes.MachineInventoryRecipeWrapper;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeTemplate;
import com.quantumshark.testmod.recipes.RecipeTemplateComponent;
import com.quantumshark.testmod.utill.MachineItemHandler;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public abstract class MachineTileEntitySingleRecipeTypeBase extends MachineTileEntityBase {
	public MachineTileEntitySingleRecipeTypeBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
		inputSlotCount = getRecipeTemplate().getInputs().size() + getNonRecipeInputSlotCount();
		outputSlotCount = getRecipeTemplate().getOutputs().size() + getNonRecipeOutputSlotCount();
		inventory = new MachineItemHandler(inputSlotCount + outputSlotCount, this);
	}
	
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
	
	// todo: cache?
	@Override
	public SlotWrapper[] getInputSlots(MachineRecipeBase recipe) {
		SlotWrapper[] ret = new SlotWrapper[getRecipeTemplate().getInputs().size()];
		int itemIndex = 0;
		// int fluidIndex = 0;
		for(int i=0;i<ret.length;++i) {
			RecipeTemplateComponent rtc = getRecipeTemplate().getInputs().get(i);
			switch(rtc.getComponentType() ) {
				case Item:
					ret[i] = new SlotWrapperItem(itemIndex, rtc.getName());
					++itemIndex;
					break;
				case Fluid:
					// todo: handle this
					break;
				default:
					// panic!
					break;
			}
		}
		return ret;
	}
	
	// todo: cache?
	@Override
	public SlotWrapper[] getOutputSlots(MachineRecipeBase recipe) {
		SlotWrapper[] ret = new SlotWrapper[getRecipeTemplate().getOutputs().size()];
		int itemIndex = 0;
		// int fluidIndex = 0;
		for(int i=0;i<ret.length;++i) {
			RecipeTemplateComponent rtc = getRecipeTemplate().getOutputs().get(i);
			switch(rtc.getComponentType() ) {
				case Item:
					ret[i] = new SlotWrapperItem(itemIndex + inputSlotCount, rtc.getName());
					++itemIndex;
					break;
				case Fluid:
					// todo: handle this
					break;
				default:
					// panic!
					break;
			}
		}
		return ret;
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
