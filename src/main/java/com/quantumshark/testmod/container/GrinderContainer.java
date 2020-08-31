package com.quantumshark.testmod.container;

import java.util.Objects;

import com.quantumshark.testmod.tileentity.GrinderTileEntity;
import com.quantumshark.testmod.utill.FunctionalIntReferenceHolder;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

public class GrinderContainer extends MachineContainerBase<GrinderTileEntity> {

	public FunctionalIntReferenceHolder currentSmeltTime;

	// Server Constructor
	public GrinderContainer(final int windowID, final PlayerInventory playerInv,
			final GrinderTileEntity tile) {
		super(RegistryHandler.GRINDER_CONTAINER.get(), windowID, playerInv, tile);

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

	@OnlyIn(Dist.CLIENT)
	public int getSmeltProgressionScaled() {
		return this.currentSmeltTime.get() != 0 && this.currentSmeltTime.get() != 0
				? this.currentSmeltTime.get() * 24 / this.tileEntity.maxSmeltTime
				: 0;
	}
}
