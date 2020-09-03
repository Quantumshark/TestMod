package com.quantumshark.testmod.capability;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class HeatCapabilityProvider implements IHeatCapability, INBTSerializable<CompoundNBT> {
	public HeatCapabilityProvider(double heatCapacity, double conductivity, double dissipation) {
		this.heatCapacity = heatCapacity;
		this.conductivity = conductivity;
		this.dissipation = dissipation;
	}

	// properties of the item
	private double heatCapacity; // joules to raise temp by 1 degree (kelvin)
	private double conductivity; // joules transferred per tick per 1 degree (kelvin) temperature differential
	private double dissipation; // joules transferred per tick per 1 degree (kelvin) temperature differential
								// with ambient
	// state
	// note: when creating in world, set temperature to ambient.
	// load and save this alongside properties
	private double temperatureK; // clearly, must be >= 0

	// calculation variables
	private boolean oddEven = false;
	private double heatAbsorbed; // joules to add this tick (-ve if we're getting colder)

	private void AddHeatDir(World world, BlockPos pos, Direction d) {
		// get adjacent block entity
		TileEntity tile = world.getTileEntity(pos.offset(d));
		if (tile == null || !(tile instanceof ICapabilityProvider)) {
			return;
		}

		// get it's heat capability, if any
		IHeatCapability other = ((ICapabilityProvider) tile).getCapability(RegistryHandler.CAPABILITY_HEAT,
				d.getOpposite()).orElse(null);
		if (other == null) {
			return;
		}

		// if it has one, get min of their conductivity and multiply by delta t
		double cond = Math.min(conductivity, other.getConductivity());
		double deltaT = other.getTemperatureK() - temperatureK;
		double deltaJ = cond * deltaT;
		heatAbsorbed += deltaJ;
		other.addTickHeat(-deltaJ);
	}
	
	private static double getAmbientTemperature(World world, BlockPos pos) {
		// todo: calculate this based on biome, altitude, and time-of-day.
		return 293;
	}

	public void tick(World world, BlockPos pos) {
		// don't run this on the client, just run it on the server.
		if(world.isRemote)
		{
			return;
		}
		// todo: ideally, replace this with a global flag somehow
		if (oddEven) {
			if (world != null) {
				AddHeatDir(world, pos, Direction.NORTH);
				AddHeatDir(world, pos, Direction.WEST);
				AddHeatDir(world, pos, Direction.DOWN);
				
				// get ambinent temperature
				// get delta t and multiply by dissipation
				double deltaT = getAmbientTemperature(world, pos) - temperatureK;
				double deltaJ = dissipation * deltaT;
				heatAbsorbed += deltaJ;
			}
//			TestMod.LOGGER.debug(String.format("A: @%d %d %d absorb %f", pos.getX(), pos.getY(), pos.getZ(), heatAbsorbed));
		}
		else
		{
			// update our temperature
			// todo: rather than this linear method, try to model a logarithmic decay model
			// exactly?
			temperatureK += heatAbsorbed / heatCapacity;
			heatAbsorbed = 0;
//			TestMod.LOGGER.debug(String.format("B: @%d %d %d temp %f", pos.getX(), pos.getY(), pos.getZ(), temperatureK));
		}
		oddEven = !oddEven;
	}

	@Override
	public double getHeatCapacity() {
		return heatCapacity;
	}

	@Override
	public double getConductivity() {
		return conductivity;
	}

	@Override
	public double getDissipation() {
		return dissipation;
	}

	@Override
	public double getTemperatureK() {
		return temperatureK;
	}

	@Override
	public void addTickHeat(double heatJ) {
		heatAbsorbed += heatJ;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("HeatCapacity", heatCapacity);
		nbt.putDouble("Conductivity", conductivity);
		nbt.putDouble("Dissipation", dissipation);
		nbt.putDouble("TemperatureK", temperatureK);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		heatCapacity = nbt.getDouble("HeatCapacity");
		conductivity = nbt.getDouble("Conductivity");
		dissipation = nbt.getDouble("Dissipation");
		temperatureK = nbt.getDouble("TemperatureK");
	}
}
