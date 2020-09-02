package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;

public interface IMachineRecipe extends IRecipe<MachineInventoryRecipeWrapper> {
	@Override
	default boolean canFit(int width, int height) {
		return false;
	}

	void read(JsonObject json);

	void read(PacketBuffer buffer);

	void write(PacketBuffer buffer);
}
