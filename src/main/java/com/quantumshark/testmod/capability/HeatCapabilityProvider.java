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
			minecraftTemp += (64.0F - (float)pos.getY()) * 0.05F / 30.0F;
		}
		ret += minecraftTemp * MINECRAFT_TEMP_CONVERSION_SCALE;	
		double dailyRange = 10;	// this will be plus or minus
		
		// you now have a temp (in K) and daily variation (+-) based on minecraft biomes. 
		
		// put code like this in to pick out specific biomes or biomes with a particular "type" (they can have many):
		// pick out a very specific biome type?
		if(biome == Biomes.SNOWY_TUNDRA)
		{
			minecraftTemp = -13F;
			dailyRange = 10;
		}
		else if(biome == Biomes.ICE_SPIKES)
		{
			minecraftTemp = -7F;
			dailyRange = 8;
		}
		else if(biome == Biomes.SNOWY_TAIGA || biome == Biomes.SNOWY_TAIGA_HILLS)
		{
			minecraftTemp = 0F;
			dailyRange = 5;
		}
		else if(biome == Biomes.SNOWY_TAIGA_MOUNTAINS)
		{
			minecraftTemp = -3F;
			dailyRange = 5;
		}
		else if(biome == Biomes.FROZEN_RIVER)
		{
			minecraftTemp = -5F;
			dailyRange = 4;
		}
		else if(biome == Biomes.SNOWY_BEACH)
		{
			minecraftTemp = -1F;
			dailyRange = 3;
		}
		else if(biome == Biomes.MOUNTAINS || biome == Biomes.GRAVELLY_MOUNTAINS || biome == Biomes.MODIFIED_GRAVELLY_MOUNTAINS)
		{
			minecraftTemp = 2F;
			dailyRange = 9;
		}
		else if(biome == Biomes.WOODED_MOUNTAINS)
		{
			minecraftTemp = -5F;
			dailyRange = 4;
		}
		else if(biome == Biomes.TAIGA || biome == Biomes.TAIGA_HILLS)
		{
			minecraftTemp = 3F;
			dailyRange = 6;
		}
		else if(biome == Biomes.TAIGA_MOUNTAINS)
		{
			minecraftTemp = 1F;
			dailyRange = 6;
		}
		else if(biome == Biomes.GIANT_TREE_TAIGA || biome == Biomes.GIANT_TREE_TAIGA_HILLS)
		{
			minecraftTemp = 4F;
			dailyRange = 6;
		}
		else if(biome == Biomes.GIANT_SPRUCE_TAIGA || biome == Biomes.GIANT_SPRUCE_TAIGA_HILLS)
		{
			minecraftTemp = 5F;
			dailyRange = 6;
		}
		else if(biome == Biomes.STONE_SHORE)
		{
			minecraftTemp = 7F;
			dailyRange = 4;
		}
		else if(biome == Biomes.PLAINS || biome == Biomes.SUNFLOWER_PLAINS)
		{
			minecraftTemp = 12F;
			dailyRange = 7;
		}
		else if(biome == Biomes.FOREST)
		{
			minecraftTemp = 12F;
			dailyRange = 2;
		}
		else if(biome == Biomes.FLOWER_FOREST)
		{
			minecraftTemp = 12F;
			dailyRange = 3;
		}
		else if(biome == Biomes.BIRCH_FOREST || biome == Biomes.BIRCH_FOREST_HILLS)
		{
			minecraftTemp = 11F;
			dailyRange = 2;
		}
		else if(biome == Biomes.TALL_BIRCH_FOREST)
		{
			minecraftTemp = 10F;
			dailyRange = 3;
		}
		else if(biome == Biomes.DARK_FOREST || biome == Biomes.DARK_FOREST_HILLS)
		{
			minecraftTemp = 12F;
			dailyRange = 1;
		}
		else if(biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS)
		{
			minecraftTemp = 17F;
			dailyRange = 2;
		}
		else if(biome == Biomes.JUNGLE || biome == Biomes.JUNGLE_HILLS)
		{
			minecraftTemp = 29F;
			dailyRange = 2;
		}
		else if(biome == Biomes.MODIFIED_JUNGLE)
		{
			minecraftTemp = 27F;
			dailyRange = 3;
		}
		else if(biome == Biomes.JUNGLE_EDGE)
		{
			minecraftTemp = 25F;
			dailyRange = 3;
		}
		else if(biome == Biomes.MODIFIED_JUNGLE_EDGE)
		{
			minecraftTemp = 24F;
			dailyRange = 3;
		}
		else if(biome == Biomes.BAMBOO_JUNGLE || biome == Biomes.BAMBOO_JUNGLE_HILLS)
		{
			minecraftTemp = 27F;
			dailyRange = 2;
		}
		else if(biome == Biomes.BEACH)
		{
			minecraftTemp = 15F;
			dailyRange = 5;
		}
		else if(biome == Biomes.MUSHROOM_FIELDS)
		{
			minecraftTemp = 25F;
			dailyRange = 1;
		}
		else if(biome == Biomes.MUSHROOM_FIELD_SHORE)
		{
			minecraftTemp = 23F;
			dailyRange = 1;
		}
		else if(biome == Biomes.DESERT)
		{
			minecraftTemp = 22F;
			dailyRange = 15;
		}
		else if(biome == Biomes.DESERT_LAKES)
		{
			minecraftTemp = 20F;
			dailyRange = 13;
		}
		else if(biome == Biomes.SAVANNA || biome == Biomes.SAVANNA_PLATEAU)
		{
			minecraftTemp = 20F;
			dailyRange = 10;
		}
		else if(biome == Biomes.SHATTERED_SAVANNA || biome == Biomes.SHATTERED_SAVANNA_PLATEAU)
		{
			minecraftTemp = 18F;
			dailyRange = 10;
		}
		else if(biome == Biomes.BADLANDS || biome == Biomes.BADLANDS_PLATEAU || biome == Biomes.MODIFIED_BADLANDS_PLATEAU)
		{
			minecraftTemp = 20F;
			dailyRange = 13;
		}
		else if(biome == Biomes.ERODED_BADLANDS)
		{
			minecraftTemp = 19F;
			dailyRange = 13;
		}
		else if(biome == Biomes.WOODED_BADLANDS_PLATEAU || biome == Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU)
		{
			minecraftTemp = 19F;
			dailyRange = 9;
		}
		else if(biome == Biomes.WARM_OCEAN || biome == Biomes.DEEP_WARM_OCEAN)
		{
			minecraftTemp = 21F;
			dailyRange = 1;
		}
		else if(biome == Biomes.LUKEWARM_OCEAN || biome == Biomes.DEEP_LUKEWARM_OCEAN)
		{
			minecraftTemp = 18F;
			dailyRange = 1;
		}
		else if(biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN)
		{
			minecraftTemp = 13F;
			dailyRange = 1;
		}
		else if(biome == Biomes.COLD_OCEAN || biome == Biomes.DEEP_COLD_OCEAN)
		{
			minecraftTemp = 7F;
			dailyRange = 1;
		}
		else if(biome == Biomes.FROZEN_OCEAN || biome == Biomes.DEEP_FROZEN_OCEAN)
		{
			minecraftTemp = 0F;
			dailyRange = 1;
		}
		else if(biome == Biomes.NETHER)
		{
			minecraftTemp = 40F;
			dailyRange = 0;
		}
		else if(biome == Biomes.END_BARRENS || biome == Biomes.END_HIGHLANDS || biome == Biomes.END_MIDLANDS || biome == Biomes.SMALL_END_ISLANDS || biome == Biomes.THE_END)
		{
			minecraftTemp = 10F;
			dailyRange = 0;
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
