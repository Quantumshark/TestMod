package com.quantumshark.testmod.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class TankItem extends ItemBase {
	private int capacity;

	public TankItem(int capacity) {
		this.capacity = capacity;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new TankItemCapabilityProvider(stack, capacity);
	}

	public class TankItemCapabilityProvider implements ICapabilityProvider {
		public TankItemCapabilityProvider(ItemStack stack, int capacity) {
			contents = new FluidHandlerItemStack(stack, capacity);
		}

		private FluidHandlerItemStack contents;

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
				return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap,
						LazyOptional.of(() -> this.contents));
			}
			return LazyOptional.empty();
		}
	}
}
