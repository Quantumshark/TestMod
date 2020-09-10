package com.quantumshark.testmod.recipes;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase.SlotWrapper;
import com.quantumshark.testmod.utill.IItemDropper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class RecipeAndWrapper<T extends MachineRecipeBase> {
	public T recipe;
	public MachineInventoryRecipeWrapper wrapper;

	public RecipeAndWrapper(T recipe, MachineInventoryRecipeWrapper wrapper) {
		this.recipe = recipe;
		this.wrapper = wrapper;
	}

	public boolean process(boolean simulate, IItemDropper itemDropper) {
		return process(simulate, itemDropper, null);
	}
	
	public boolean process(boolean simulate, IItemDropper itemDropper, IRecipeTagMerge rtm) {
		boolean ret = true;
		
		// note: this isn't transactional. We assume the recipe will work - if the first output succeeds and the second fails,
		// we get the first output for free.
		for(int i=0;i<recipe.getOutputs().size();++i)
		{
			SlotWrapper inputTagSource = null;
			if(rtm != null)
			{
				int inputTagSourceId = rtm.getTagSource(i);
				if(inputTagSourceId >= 0)
				{
					inputTagSource = wrapper.getInputSlot(inputTagSourceId);
				}
			}
			RecipeComponent output = recipe.getOutputs().get(i);
			SlotWrapper sw = wrapper.getOutputSlot(i);
			if(sw == null) {
				// not currently mapped
				continue;
			}
			ret &= sw.insert(output, simulate, inputTagSource);
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
