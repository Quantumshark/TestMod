package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.blocks.state.LitStateHandler;
import com.quantumshark.testmod.capability.HeatCapabilityProvider;
import com.quantumshark.testmod.container.SolidFuelHeaterContainer;
import com.quantumshark.testmod.utill.MachineItemHandler;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

// this doesn't have recipes, so may need an intermediate class for ones that do
public class SolidFuelHeaterTileEntity extends MachineTileEntityBase {
	// state variables
	private int currentBurnTime; // the number of ticks for which we've been burning the current item
	private ItemStack currentBurnItem; // nullable. The item (qty 1, already removed from input slot) we're currently
										// burning through
	private int currentBurnItemDuration; // the number of ticks that the current burn item will burn for
	private double currentBurnItemPower; // the number of Joules per tick in heat generated during burning

	public SolidFuelHeaterTileEntity() {
		super(RegistryHandler.SOLID_FUEL_HEATER_TILE_ENTITY.get());
		inputSlotCount = 1;
		inventory = new MachineItemHandler(inputSlotCount + outputSlotCount, this);
		heat = new HeatCapabilityProvider(200000, 5000, 550);
	}

	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new SolidFuelHeaterContainer(windowID, playerInv, this);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.world == null || this.world.isRemote) {
			return;
		}

		// note: super.tick() will have updated temperature already.
		// heat transfer runs every other tick due to odd/even, but this runs every tick
		// so we could do a temperature check here - either explode or shutdown if too
		// hot?
		// tick pattern will work as follows:
		// heat A (neighbours / environment)
		// burn
		// heat B (recalculate temp)
		// burn
		// so recalc will include two burns.

		boolean dirty = false;
		boolean isRunning = false;

		if (true || world.isBlockPowered(getPos())) { // todo: configurable redstone control mode
			if (currentBurnItem == null) {
				ItemStack fuel = inventory.getStackInSlot(0);
				if (!fuel.isEmpty()) // double-check it can burn
				{
					int furnaceBurnTime = getFuelBurnTime(fuel);
					if (furnaceBurnTime > 0) {
						// if there's an input stack, pick one item off it
						// copy that instance into currentBurnItem
						currentBurnItem = inventory.extractItem(0, 1, false);

						// deduce it's properties
						currentBurnTime = 0;
						// 3 and 0.7 gives a spread from 46 ticks @ 10.8k (bamboo) to 2630 ticks @ 60.8k
						// (coal block)
						// these numbers control the split between longer burn and more power (heat per
						// tick)
						currentBurnItemDuration = (int) (3 * Math.pow(furnaceBurnTime, 0.7));
						// the 10000f constant here determines how much heat you get per tick of burning
						// fuel
						// so varying from 25x for bamboo to 16k x for coal block
						currentBurnItemPower = furnaceBurnTime * 37500f / currentBurnItemDuration;
						// todo: if input was a bucket of lava (e.g.), then we need to spit out a bucket
						// here.
					}
				}
			}
			if (currentBurnItem != null) {
				isRunning = true;
				heat.addTickHeat(currentBurnItemPower);
				++currentBurnTime;
				if (currentBurnTime >= currentBurnItemDuration) {
					// finished burning this thing.
					currentBurnItem = null;
					currentBurnTime = 0;
					currentBurnItemPower = 0;
					currentBurnItemDuration = 0;
				}
			}
		}
		BlockState oldBlockState = getBlockState();
		boolean wasRunning = oldBlockState.get(LitStateHandler.LIT);
		if (isRunning != wasRunning) {
			world.setBlockState(getPos(), getBlockState().with(LitStateHandler.LIT, isRunning));
			dirty = true;
		}

		if (dirty) {
			markDirty();
			world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + TestMod.MOD_ID + ".solid_fuel_heater");
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		currentBurnTime = compound.getInt("CurrentBurnTime");
		if (compound.contains("CurrentBurnItem")) {
			currentBurnItem = ItemStack.read(compound.getCompound("CurrentBurnItem"));
		} else {
			currentBurnItem = null;
		}
		currentBurnItemDuration = compound.getInt("CurrentBurnItemDuration");
		currentBurnItemPower = compound.getDouble("CurrentBurnItemPower");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("CurrentBurnTime", currentBurnTime);
		if (currentBurnItem != null) {
			compound.put("CurrentBurnItem", currentBurnItem.serializeNBT());
		}
		compound.putInt("CurrentBurnItemDuration", currentBurnItemDuration);
		compound.putDouble("CurrentBurnItemPower", currentBurnItemPower);

		return compound;
	}

	// this stuff could be in a class.
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return getFuelBurnTime(stack) > 0;
	}

	public float getBurnProgression() {
		if (currentBurnItem == null || currentBurnItemDuration == 0) {
			return 0;
		}
		return (float) currentBurnTime / currentBurnItemDuration;
	}

	public static int getFuelBurnTime(ItemStack stack) {
		int ret = stack.getBurnTime();
		if (ret == -1) {
			return net.minecraftforge.common.ForgeHooks.getBurnTime(stack);
		}
		return ret;
	}
}
