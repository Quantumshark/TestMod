package com.quantumshark.testmod.capability;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
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
		double deltaJ = getHeatFromDeltaT(deltaT, cond);
		heatAbsorbed += deltaJ;
		other.addTickHeat(-deltaJ);
	}
	
	// something to multiply minecraft temp by to convert it to degrees C (assuming zero is freezing)
	private static final double MINECRAFT_TEMP_CONVERSION_SCALE = 20.f;
	private static final double FREEZING_WATER = 273; 
	
	private static double getAmbientTemperature(World world, BlockPos pos) {
		// calculate this based on biome, altitude, and time-of-day.
		Biome biome = world.getBiome(pos);
		// the value we're going to return.
		double ret = FREEZING_WATER;	// start at freezing point
		
		// 1. get vanilla minecraft estimate of temperature, scaled to degrees C
		double minecraftTemp = biome.getTemperatureRaw(pos);	// this calc includes some noise based on position, and also height factor if above zero
		// note: biome.getTemperature(pos) I think also includes localised heat sources like torches
		// could use biome.getDefaultTemperature() and do the noise and height ourselves?
		// extrapolate the minecraft temp calc for below sea level, as minecraft doesn't. 
		if(pos.getY() < 64)
		{
			minecraftTemp += ((float)pos.getY() - 64.0F) * 0.05F / 30.0F;
		}
		ret += minecraftTemp * MINECRAFT_TEMP_CONVERSION_SCALE;	
		double dailyRange = 10;	// this will be plus or minus
		
		// you now have a temp (in K) and daily variation (+-) based on minecraft biomes. 
		
		// put code like this in to pick out specific biomes or biomes with a particular "type" (they can have many):
		// pick out a very specific biome type?
		if(biome == Biomes.ICE_SPIKES)
		{
			
		}
		// pick out biomes matching a characteristic
		if(BiomeDictionary.hasType(biome, Type.WET))
		{
			
		}
		double dayTime = Math.PI * 2 * world.getDayTime()/24000f;
		ret -= dailyRange * Math.cos(dayTime);	// coldest at dawn but warmest at dusk? close enough
		return ret;
	}

	public void tick(World world, BlockPos pos) {
		double ambientTemp = getAmbientTemperature(world, pos);
		
		// this is a bit of a fudge to set temp to ambient when block placed
		if(temperatureK == 0)
		{
			temperatureK = ambientTemp;
		}
		
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
				double deltaT = ambientTemp - temperatureK;
				double deltaJ = getHeatFromDeltaT(deltaT, dissipation);
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

	// calculate heat transfer between two objects (or an object and its environment)
	// deltaT is difference in temperature measured in Kelvin (identical to difference in C)
	// rateConstant is either conductivity or dissipation, the number of joules transferred per tick per degree difference in temp.
	private double getHeatFromDeltaT(double deltaT, double rateConstant) {
		//return rateConstant * deltaT;

		// model this based on exponential decay rather than linear
		// avoid dbz
		if(deltaT == 0) {
			return 0;
		}
		double k2 = rateConstant / heatCapacity;
		
		double tempChange = deltaT * (1-Math.exp(-2*k2));
		
		return(heatCapacity * tempChange); 
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
