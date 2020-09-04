package com.quantumshark.testmod.container;

import java.util.Objects;

import com.quantumshark.testmod.client.gui.ProgressArrowWidget;
import com.quantumshark.testmod.client.gui.TemperatureWidget;
import com.quantumshark.testmod.tileentity.SolidFuelHeaterTileEntity;
import com.quantumshark.testmod.utill.FunctionalIntReferenceHolder;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.SlotItemHandler;

public class SolidFuelHeaterContainer extends MachineContainerBase<SolidFuelHeaterTileEntity> {

	public FunctionalIntReferenceHolder currentSmeltTime;

	// Server Constructor
	public SolidFuelHeaterContainer(final int windowID, final PlayerInventory playerInv,
			final SolidFuelHeaterTileEntity tile) {
		super(RegistryHandler.SOLID_FUEL_HEATER_CONTAINER.get(), windowID, playerInv, tile);

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

		// Input slot
		this.addSlot(new SlotItemHandler(tile.getInventory(), 0, 32, 33));
		
		// widgets
		screenWidgets.add(new TemperatureWidget(140,8,getTileEntity().getHeat()));
		// add a burn widget dead centre that shows the currently burning item, and over the top a rising flame item, possibly under a grate
		screenWidgets.add(new ProgressArrowWidget(55, 33, ()->getBurnProgression()));
	}

	// Client Constructor
	public SolidFuelHeaterContainer(final int windowID, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowID, playerInv, getTileEntity(playerInv, data));
	}

	private static SolidFuelHeaterTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof SolidFuelHeaterTileEntity) {
			return (SolidFuelHeaterTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getBurnProgression() {
		return tileEntity.getBurnProgression();
	}
}
