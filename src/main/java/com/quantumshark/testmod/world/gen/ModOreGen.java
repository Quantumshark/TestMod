package com.quantumshark.testmod.world.gen;

import net.minecraftforge.registries.ForgeRegistries;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;

public class ModOreGen {
	public static void generateOres() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			if (biome != Biomes.NETHER) {
				// freq, from-bottom, from-surface, max
				ConfiguredPlacement customConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(1, 32, 5, 20));
				OreFeatureConfig ofc = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
						RegistryHandler.BLUECRYSTALORE_BLOCK.get().getDefaultState(), 32);
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
						Feature.ORE.withConfiguration(ofc).withPlacement(customConfig)); // 16 = vein size
				ConfiguredPlacement gritConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(9, 72, 1, 253));
				OreFeatureConfig gritofc = new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
						RegistryHandler.MILLSTONEGRIT_BLOCK.get().getDefaultState(), 62);
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
						Feature.ORE.withConfiguration(gritofc).withPlacement(gritConfig)); // 16 = vein size
			}
		}
	}
}
