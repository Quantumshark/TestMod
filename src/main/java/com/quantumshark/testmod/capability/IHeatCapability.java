package com.quantumshark.testmod.capability;

public interface IHeatCapability {
	// item properties
	double getHeatCapacity(); // joules to raise temp by 1 degree (kelvin)

	double getConductivity(); // joules transferred per tick per 1 degree (kelvin) temperature differential

	double getDissipation(); // joules transferred per tick per 1 degree (kelvin) temperature differential
								// with ambient
	// state
	double getTemperatureK();
	
	void addTickHeat(double heatJ);
}
