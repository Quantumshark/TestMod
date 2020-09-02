package com.quantumshark.testmod.container;

import java.util.Objects;

import com.quantumshark.testmod.client.gui.IScreenWidget;
import com.quantumshark.testmod.client.gui.ProgressArrowWidget;
import com.quantumshark.testmod.tileentity.BlastFurnaceTileEntity;
import com.quantumshark.testmod.utill.FunctionalIntReferenceHolder;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

public class BlastFurnaceContainer extends MachineContainerBase<BlastFurnaceTileEntity> {

	public FunctionalIntReferenceHolder currentSmeltTime;
	
	// Server Constructor
	public BlastFurnaceContainer(final int windowID, final PlayerInventory playerInv,
			final BlastFurnaceTileEntity tile) {
		super(RegistryHandler.BLAST_FURNACE_CONTAINER.get(), windowID, playerInv, tile);

		// this class. Sets up slots. 
		// could have a base method for player inventory though (perhaps with an offset x and y)
		final int rowPitch = 18;
		final int columnPitch = 18;
		final int startX = 8;

		// Hotbar
		int hotbarY = 142;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInv, column, startX + (column * columnPitch), hotbarY));
		}

		// Main Player Inventory
		final int startY = 84;

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * columnPitch),
						startY + (row * rowPitch)));
			}
		}

		// Input Slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 56, 14));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 56, 34));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 2, 56, 54));
		// Output slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 3, 116, 20));
		this.addSlot(new SlotItemHandler(tile.getInventory(), 3, 116, 50));

		// widgets
		screenWidgets.add(new ProgressArrowWidget(79, 33, ()->getSmeltProgression()));
		
		this.trackInt(currentSmeltTime = new FunctionalIntReferenceHolder(() -> this.getTileEntity().currentSmeltTime,
				value -> this.getTileEntity().currentSmeltTime = value));
	}

	// Client Constructor
	public BlastFurnaceContainer(final int windowID, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowID, playerInv, getTileEntity(playerInv, data));
	}

	private static BlastFurnaceTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof BlastFurnaceTileEntity) {
			return (BlastFurnaceTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}

	@OnlyIn(Dist.CLIENT)
	public float getSmeltProgression() {
		return this.currentSmeltTime.get() != 0 && this.currentSmeltTime.get() != 0
				? this.currentSmeltTime.get() * 1.f / this.getTileEntity().maxSmeltTime
				: 0;
	}
}
