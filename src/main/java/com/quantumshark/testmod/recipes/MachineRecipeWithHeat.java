package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public abstract class MachineRecipeWithHeat extends MachineRecipeWithTime{
	private int minTemp = 0;
	private int maxTemp = 99999;
	private int heatProd = 0;
	private int deltaTheta = 0;	// flag for no speedup

	public MachineRecipeWithHeat(ResourceLocation id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void read(JsonObject json) {
		super.read(json);
		JsonObject heat = JSONUtils.getJsonObject(json, "heat");
		minTemp = JSONUtils.getInt(heat, "min",0);
		maxTemp = JSONUtils.getInt(heat, "max",99999);
		heatProd = JSONUtils.getInt(heat, "heatprod",0);
		deltaTheta = JSONUtils.getInt(heat, "deltatheta",0);
	}

	@Override
	public void read(PacketBuffer buffer) {
		super.read(buffer);
		minTemp = buffer.readInt();
		maxTemp = buffer.readInt();
		heatProd = buffer.readInt();
		deltaTheta = buffer.readInt();
	}

	@Override
	public void write(PacketBuffer buffer) {
		super.write(buffer);
		buffer.writeInt(getMinTemp());
		buffer.writeInt(getMaxTemp());
		buffer.writeInt(getHeatProd());
		buffer.writeInt(getDeltaTheta());
	}

	public int getHeatProd() {
		return heatProd;
	}

	public int getMaxTemp() {
		return maxTemp;
	}

	public int getMinTemp() {
		return minTemp;
	}

	public int getDeltaTheta() {
		return deltaTheta;
	}		
}
