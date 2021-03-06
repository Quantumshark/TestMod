package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.blocks.state.LitStateHandler;
import com.quantumshark.testmod.capability.ShaftPowerDefImpl;
import com.quantumshark.testmod.container.FlotationSeparatorContainer;
import com.quantumshark.testmod.recipes.FlotationSeparatorRecipe;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeAndWrapper;
import com.quantumshark.testmod.recipes.RecipeTemplate;
import com.quantumshark.testmod.utill.RecipeInit;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

public class FlotationSeparatorTileEntity extends MachineTileEntitySingleRecipeTypeBase {
	private ShaftPowerDefImpl shaft;

	public int currentSmeltTime;
	public final int maxSmeltTime = 100;

	public FlotationSeparatorTileEntity() {
		super(RegistryHandler.FLOTATION_SEPARATOR_TILE_ENTITY.get());
	}

	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new FlotationSeparatorContainer(windowID, playerInv, this);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.world == null || this.world.isRemote) {
			return;
		}

		boolean dirty = false;
		boolean isRunning = false;

		if (getRedstoneRunnable()) {
			// todo: save the recipe id in the state so we don't have to find every tick.
			// also so we can lose progess if you: turn machine on; put something in; turn
			// machine off; take input out; put in new input; turn back on again
			RecipeAndWrapper<FlotationSeparatorRecipe> match = this.findMatchingRecipe();
			if (match == null) {
				// reset progress if you remove the input item.
				this.currentSmeltTime = 0;
			} else {
				if (match.process(true, null)) // pass a null dropper so it crashes if it tries to drop something :)
				{
					if (this.currentSmeltTime < this.maxSmeltTime) {
						isRunning = true;
						this.currentSmeltTime++;
					} else {
						this.currentSmeltTime = 0;

						match.process(false, new ItemDropper());

						dirty = true;
					}
				}
			}
		}
		BlockState oldBlockState = getBlockState();
		boolean wasRunning = oldBlockState.get(LitStateHandler.LIT);
		if (isRunning != wasRunning) {
			this.world.setBlockState(this.getPos(), this.getBlockState().with(LitStateHandler.LIT, isRunning));
			world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockState().getBlock());
			dirty = true;
		}

		boolean br = AttemptFillBucket(1, 0, 4);
		dirty |= br;
		if (br) {
			TestMod.LOGGER.debug("after attempt fill: tank contains " + fluidInventory.getFluidInTank(0).getAmount() +" of "+ fluidInventory.getFluidInTank(0).getTranslationKey());
		}
		br = AttemptEmptyBucket(1, 0, 4);
		dirty |= br;
		if (br) {
			TestMod.LOGGER.debug("after attempt empty: tank contains " + fluidInventory.getFluidInTank(0).getAmount() +" of "+ fluidInventory.getFluidInTank(0).getTranslationKey());
		}
		if (dirty) {
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(),
					Constants.BlockFlags.BLOCK_UPDATE);
		}
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + TestMod.MOD_ID + ".flotation_separator");
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		this.currentSmeltTime = compound.getInt("CurrentSmeltTime");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("CurrentSmeltTime", this.currentSmeltTime);

		return compound;
	}

	@Override
	protected IRecipeType<MachineRecipeBase> getRecipeType() {
		return RecipeInit.FLOTATION_SEPARATOR_RECIPE_TYPE;
	}

	// the recipe template (combination of inputs and secondary outputs if any)
	@Override
	public RecipeTemplate getRecipeTemplate() {
		return RecipeTemplate.FLOTATION_SEPARATOR;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == RegistryHandler.CAPABILITY_SHAFT_POWER) {
			if (side == Direction.UP) {
				return RegistryHandler.CAPABILITY_SHAFT_POWER.orEmpty(cap, LazyOptional.of(() -> this.shaft));
			} else
				return null;
		}
		return super.getCapability(cap, side);
	}

	// bucket in
	@Override
	protected int getNonRecipeInputSlotCount() {
		return 1;
	}

	// filled bucket out
	@Override
	protected int getNonRecipeOutputSlotCount() {
		return 1;
	}

	// this stuff could be in a class.
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot == 1) {
			return isItemValidForFluidSlot(slot, 0, stack, true, true);
			/*
			if (stack.getItem() == Items.BUCKET
					|| (stack.getContainerItem() != null && stack.getContainerItem().getItem() == Items.BUCKET)) {
				return true;
			}
			IFluidHandlerItem h = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
					.orElse(null);
			if (h != null) {
				return true;
			}
			return false;
			*/
		} else {
			return super.isItemValid(slot, stack);
		}
	}
}
