package com.quantumshark.testmod.recipes;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase.SlotWrapper;
import com.quantumshark.testmod.utill.IItemDropper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeAndWrapper {
	public MachineRecipeBase recipe;
	public MachineInventoryRecipeWrapper wrapper;

	public RecipeAndWrapper(MachineRecipeBase recipe, MachineInventoryRecipeWrapper wrapper) {
		this.recipe = recipe;
		this.wrapper = wrapper;
	}

	public boolean process(boolean simulate, IItemDropper itemDropper) {
		boolean ret = true;
		
		for(int i=0;i<recipe.getOutputs().size();++i)
		{
			RecipeComponent output = recipe.getOutputs().get(i);
			SlotWrapper sw = wrapper.getOutputSlot(i);
			if(sw == null) {
				// not currently mapped
				continue;
			}
			ret &= sw.insert(output, simulate);
		}
		
		if (!simulate) {
			NonNullList<ItemStack> otherOutputs = recipe.getRemainingItems(wrapper);
			for (int i = 0; i < recipe.getInputs().size(); ++i) {
				RecipeComponent input = recipe.getInputs().get(i);
				SlotWrapper sw = wrapper.getInputSlot(i);
				if(sw != null) {
					sw.decrease(input);
				}
				
				// drop any left-over buckets
				// totally-untested-and-highly-dangerous
				ItemStack dropBucket = otherOutputs.get(i);
				if (dropBucket != null && dropBucket != ItemStack.EMPTY) {
					itemDropper.DropItem(dropBucket.copy());
				}
			}
		}

		return ret;
	}
}
