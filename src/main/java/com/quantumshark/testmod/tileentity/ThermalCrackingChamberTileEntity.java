package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.blocks.state.LitStateHandler;
import com.quantumshark.testmod.container.ThermalCrackingChamberContainer;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeAndWrapper;
import com.quantumshark.testmod.recipes.RecipeTemplate;
import com.quantumshark.testmod.recipes.ThermalCrackingChamberRecipe;
import com.quantumshark.testmod.utill.RecipeInit;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

public class ThermalCrackingChamberTileEntity extends MachineTileEntitySingleRecipeTypeBase {
	public double currentSmeltTime;
	public int maxSmeltTime = 100;

	public ThermalCrackingChamberTileEntity() {
		super(RegistryHandler.THERMAL_CRACKING_CHAMBER_TILE_ENTITY.get());
	}

	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new ThermalCrackingChamberContainer(windowID, playerInv, this);
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
			RecipeAndWrapper<ThermalCrackingChamberRecipe> match = this.findMatchingRecipe();
			if (match == null) {
				// reset progress if you remove the input item.
				this.currentSmeltTime = 0;
			} else {
				ThermalCrackingChamberRecipe recipe = match.recipe;
				maxSmeltTime = recipe.getBaseTime();
				if (match.process(true, null)) // pass a null dropper so it crashes if it tries to drop something :)
				{

					if (heat.getTemperatureK() >= recipe.getMinTemp()
							&& heat.getTemperatureK() <= recipe.getMaxTemp()) {
						if (this.currentSmeltTime < maxSmeltTime) {
							isRunning = true;
							double tickTime = 1;
							int deltaTheta = recipe.getDeltaTheta();
							if (deltaTheta > 0) {
								tickTime = 1 + (heat.getTemperatureK() - recipe.getMinTemp()) / deltaTheta;
							}
							currentSmeltTime += tickTime;
						} else {
							this.currentSmeltTime = 0;

							match.process(false, new ItemDropper());
							heat.addTickHeat(recipe.getHeatProd());

							dirty = true;
						}
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

		dirty |= AttemptFillBucket(0, 0, 2);
		dirty |= AttemptEmptyBucket(0, 0, 2);
		dirty |= AttemptFillBucket(1, 1, 3);

		if (dirty) {
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(),
					Constants.BlockFlags.BLOCK_UPDATE);
		}
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + TestMod.MOD_ID + ".thermal_cracking_chamber");
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		this.currentSmeltTime = compound.getDouble("CurrentSmeltTime");
		this.maxSmeltTime = compound.getInt("MaxSmeltTime");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putDouble("CurrentSmeltTime", this.currentSmeltTime);
		compound.putInt("MaxSmeltTime", maxSmeltTime);

		return compound;
	}

	@Override
	protected IRecipeType<MachineRecipeBase> getRecipeType() {
		return RecipeInit.THERMAL_CRACKING_CHAMBER_RECIPE_TYPE;
	}

	// the recipe template (combination of inputs and secondary outputs if any)
	@Override
	public RecipeTemplate getRecipeTemplate() {
		return RecipeTemplate.THERMAL_CRACKING_CHAMBER;
	}

	// bucket in
	@Override
	protected int getNonRecipeInputSlotCount() {
		return 2;
	}

	// filled bucket out
	@Override
	protected int getNonRecipeOutputSlotCount() {
		return 2;
	}

	// this stuff could be in a class.
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot == 0) {
			return isItemValidForFluidSlot(slot, 0, stack, true, true);
		} else if (slot == 1) {
			return isItemValidForFluidSlot(slot, 1, stack, false, true);
		} else {
			return super.isItemValid(slot, stack);
		}
	}
}
