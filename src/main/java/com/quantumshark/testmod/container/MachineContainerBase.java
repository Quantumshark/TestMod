package com.quantumshark.testmod.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.quantumshark.testmod.client.gui.IScreenWidget;
import com.quantumshark.testmod.client.gui.RedstoneModeWidget;
import com.quantumshark.testmod.tileentity.MachineTileEntityBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;

public abstract class MachineContainerBase<T extends MachineTileEntityBase> extends Container {
	protected final T tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	
	public final NonNullList<IScreenWidget> screenWidgets = NonNullList.create();

	public MachineContainerBase(@Nullable ContainerType<?> type, final int windowID, final PlayerInventory playerInv,
			final T tile) {
		super(type, windowID);

		// base
		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());
		screenWidgets.add(new RedstoneModeWidget(153,19, tile));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, tileEntity.getBlockState().getBlock());
	}

	// base class?
	@Nonnull
	@Override
	public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
		// 0-8 toolbar
		// 9-35 inventory
		// 36+ machine slots.
		ItemStack returnStack = ItemStack.EMPTY;
		final Slot slot = this.inventorySlots.get(index);
		if (slot == null || !slot.getHasStack()) {
			return returnStack;
		}
		final ItemStack slotStack = slot.getStack();
		returnStack = slotStack.copy();
		
		int playerSlotCount = player.inventory.mainInventory.size();

		if (index < playerSlotCount) {	// from player to input
			if (!mergeItemStack(slotStack, playerSlotCount, this.inventorySlots.size(), false)) {	// any input or output slot
				return ItemStack.EMPTY;
			}
		} else if (!mergeItemStack(slotStack, 0, playerSlotCount, false)) {
			return ItemStack.EMPTY;
		}
		if (slotStack.getCount() == 0) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		if (slotStack.getCount() == returnStack.getCount()) {
			return ItemStack.EMPTY;
		}
		
		// note: overriding this might be good
		slot.onTake(player, slotStack);

		return returnStack;
	}

	public T getTileEntity() {
		return tileEntity;
	}
}
