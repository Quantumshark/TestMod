package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public abstract class MachineRecipeWithTime extends MachineRecipeBase {
	private int baseTime = 100;

	public MachineRecipeWithTime(ResourceLocation id) {
		super(id);
	}

	@Override
	public void read(JsonObject json) {
		super.read(json);
		baseTime = JSONUtils.getInt(json, "basetime",100);
	}

	@Override
	public void read(PacketBuffer buffer) {
		super.read(buffer);
		baseTime = buffer.readInt();
	}

	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		buffer.writeInt(getBaseTime());
	}

	public int getBaseTime() {
		return baseTime;
	}	
}
