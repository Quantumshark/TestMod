package com.quantumshark.testmod.utill;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class MachineItemHandler extends ItemStackHandler {
	private ISlotValidator slotValidator;

	public MachineItemHandler(int size, ISlotValidator slotValidator) {
		super(size);
		this.slotValidator = slotValidator;
	}

	// this is how we stop the wrong things being stuck in. 
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
    	if(slotValidator == null)
    	{
    		return true;
    	}
    	return slotValidator.isItemValid(slot, stack);
    }

    // insert a machine output item. Differs from base InsertItem as it overrides isItemValid (for output slots)
    @Nonnull
    public ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
            
        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }    
    
	@Override
	public String toString() {
		return this.stacks.toString();
	}
}
