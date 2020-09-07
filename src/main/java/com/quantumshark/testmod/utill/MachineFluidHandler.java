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
	public MachineFluidHandler(int tankCount) {
		tanks = NonNullList.create();
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
		throw new IllegalStateException("unexpected call of MachineFluidHandler");
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		throw new IllegalStateException("unexpected call of MachineFluidHandler");
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		throw new IllegalStateException("unexpected call of MachineFluidHandler");
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		throw new IllegalStateException("unexpected call of MachineFluidHandler");
	}
}
