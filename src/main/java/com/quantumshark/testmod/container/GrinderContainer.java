package com.quantumshark.testmod.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.quantumshark.testmod.tileentity.GrinderTileEntity;
import com.quantumshark.testmod.utill.FunctionalIntReferenceHolder;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

public class GrinderContainer extends Container {

	public GrinderTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;
	public FunctionalIntReferenceHolder currentSmeltTime;

	// Server Constructor
	public GrinderContainer(final int windowID, final PlayerInventory playerInv,
			final GrinderTileEntity tile) {
		super(RegistryHandler.GRINDER_CONTAINER.get(), windowID);

		this.tileEntity = tile;
		this.canInteractWithCallable = IWorldPosCallable.of(tile.getWorld(), tile.getPos());

		final int slotSizePlus2 = 18;
		final int startX = 8;

		// Hotbar
		int hotbarY = 142;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), hotbarY));
		}

		// Main Player Inventory
		final int startY = 84;

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * slotSizePlus2),
						startY + (row * slotSizePlus2)));
			}
		}

		// Furnace Slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 56, 34));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 116, 35));

		this.trackInt(currentSmeltTime = new FunctionalIntReferenceHolder(() -> this.tileEntity.currentSmeltTime,
				value -> this.tileEntity.currentSmeltTime = value));
	}

	// Client Constructor
	public GrinderContainer(final int windowID, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowID, playerInv, getTileEntity(playerInv, data));
	}

	private static GrinderTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof GrinderTileEntity) {
			return (GrinderTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, RegistryHandler.GRINDER_BLOCK.get());
	}

	@Override
	// todo: make this class much more generic (perhaps by wrapping the actual tile thing in an interface).
	// make sure we know which are our slots and which the player's.
	// also differentiate between input and output slots
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		// this (getRecipe) doesn't work quite as we expect - it ignores stack and looks at inventory. Rewrite.
		if(startIndex == 36 && !tileEntity.hasRecipe(stack))
		{
			return false;
		}
		return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
	}
	
	@Nonnull
	@Override
	public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
		// currently it's shifting from inventory slots to player slots 0-1
		// and from player slots 0-1 to both inventory slots, output first
		// and regardless of whether it's useful.
		// elsewhere also reject any insert ...
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

		final int containerSlotCount = this.inventorySlots.size() - playerSlotCount;
		if (index < playerSlotCount) {	// from player to input
			if (!mergeItemStack(slotStack, playerSlotCount, playerSlotCount+1, false)) {	// 1 input slot
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

	@OnlyIn(Dist.CLIENT)
	public int getSmeltProgressionScaled() {
		return this.currentSmeltTime.get() != 0 && this.currentSmeltTime.get() != 0
				? this.currentSmeltTime.get() * 24 / this.tileEntity.maxSmeltTime
				: 0;
	}
}
