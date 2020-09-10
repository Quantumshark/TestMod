package com.quantumshark.testmod.utill;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MachineFluidHandler implements IFluidHandler, INBTSerializable<CompoundNBT> {
	private ISlotValidator slotValidator;

	public MachineFluidHandler(int tankCount, ISlotValidator slotValidator) {
		tanks = NonNullList.create();
		this.slotValidator = slotValidator;
		for (int i = 0; i < tankCount; ++i) {
			// default size = 1 bucket
			tanks.add(new TankFluidHandler(FluidAttributes.BUCKET_VOLUME));
		}
	}

	private final NonNullList<TankFluidHandler> tanks;

	public TankFluidHandler getTank(int i) {
		return tanks.get(i);
	}

	@Override
	public CompoundNBT serializeNBT() {
		ListNBT nbtTagList = new ListNBT();
		for (int i = 0; i < tanks.size(); i++) {
//			if (!tanks.get(i).isEmpty()) {
			// We need to push empty tanks, otherwise the client doesn't get the update
			CompoundNBT itemTag = new CompoundNBT();
			itemTag.putInt("Slot", i);
			tanks.get(i).writeToNBT(itemTag);
			nbtTagList.add(itemTag);
//			}
		}
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("Tanks", nbtTagList);
		nbt.putInt("Size", tanks.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		int newSize = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : tanks.size();
		if (newSize > tanks.size()) {
			setSize(newSize);
		}
		ListNBT tagList = nbt.getList("Tanks", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < tanks.size()) {
				tanks.get(slot).setFluid(FluidStack.loadFluidStackFromNBT(itemTags));
			}
		}
	}

	private void setSize(int tankCount) {
		tanks.clear();
		for (int i = 0; i < tankCount; ++i) {
			// default size = 1 bucket
			tanks.add(new TankFluidHandler(FluidAttributes.BUCKET_VOLUME));
		}
	}

	@Override
	public int getTanks() {
		return tanks.size();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return tanks.get(tank).getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return tanks.get(tank).getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (slotValidator == null) {
			return true;
		}
		return slotValidator.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return fill(resource, action, false);
	}

	public int fill(FluidStack resource, FluidAction action, boolean jfdi) {
		for (int i = 0; i < tanks.size(); ++i) {
			if (!jfdi && !isFluidValid(i, resource)) {
				continue;
			}
			TankFluidHandler tank = tanks.get(i);
			int ret = tank.fill(resource, action);
			if (ret > 0) {
				return ret;
			}
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		for (int i = 0; i < tanks.size(); ++i) {
			TankFluidHandler tank = tanks.get(i);
			FluidStack ret = tank.drain(resource, action);
			if (!ret.isEmpty()) {
				return ret;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		for (int i = 0; i < tanks.size(); ++i) {
			TankFluidHandler tank = tanks.get(i);
			FluidStack ret = tank.drain(maxDrain, action);
			if (!ret.isEmpty()) {
				return ret;
			}
		}
		return FluidStack.EMPTY;
	}
}
