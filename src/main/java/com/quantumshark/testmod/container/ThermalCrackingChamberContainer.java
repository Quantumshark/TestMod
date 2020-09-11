package com.quantumshark.testmod.container;

import java.util.Objects;

import com.quantumshark.testmod.client.gui.ProgressArrowWidget;
import com.quantumshark.testmod.client.gui.TankWidget;
import com.quantumshark.testmod.client.gui.TemperatureWidget;
import com.quantumshark.testmod.tileentity.ThermalCrackingChamberTileEntity;
import com.quantumshark.testmod.utill.FunctionalIntReferenceHolder;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

public class ThermalCrackingChamberContainer extends MachineContainerBase<ThermalCrackingChamberTileEntity> {

	public FunctionalIntReferenceHolder currentSmeltTime;

	// Server Constructor
	public ThermalCrackingChamberContainer(final int windowID, final PlayerInventory playerInv,
			final ThermalCrackingChamberTileEntity tile) {
		super(RegistryHandler.THERMAL_CRACKING_CHAMBER_CONTAINER.get(), windowID, playerInv, tile);

		// this class. Sets up slots.
		// could have a base method for player inventory though (perhaps with an offset
		// x and y)
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
		
		// bucket slots
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 34, 10));	// bucket in
		this.addSlot(new SlotItemHandler(tile.getInventory(), 1, 126, 10));	// bucket in
		this.addSlot(new SlotItemHandler(tile.getInventory(), 2, 34, 60));	// bucket out
		this.addSlot(new SlotItemHandler(tile.getInventory(), 3, 126, 60));	// bucket out
		
		// tanks (2)
		screenWidgets.add(new TankWidget(0, 33, 29, 18, 24));
		screenWidgets.add(new TankWidget(1, 125, 29, 18, 24));
		
		// widgets
		screenWidgets.add(new ProgressArrowWidget(75, 33, () -> getSmeltProgression()));
		screenWidgets.add(new TemperatureWidget(140, 8, getTileEntity().getHeat()));

		this.trackInt(
				currentSmeltTime = new FunctionalIntReferenceHolder(() -> (int) (this.getTileEntity().currentSmeltTime),
						value -> this.getTileEntity().currentSmeltTime = value));
	}

	// Client Constructor
	public ThermalCrackingChamberContainer(final int windowID, final PlayerInventory playerInv,
			final PacketBuffer data) {
		this(windowID, playerInv, getTileEntity(playerInv, data));
	}

	private static ThermalCrackingChamberTileEntity getTileEntity(final PlayerInventory playerInv,
			final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof ThermalCrackingChamberTileEntity) {
			return (ThermalCrackingChamberTileEntity) tileAtPos;
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
