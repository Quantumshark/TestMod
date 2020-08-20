package com.quantumshark.testmod.world.gen;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;

public class ModOreGen {
	public static void generateOres() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			
			// Chalcanthite
			AddOreSpawn(biome, 4,32,5,20,RegistryHandler.BLUECRYSTALORE_BLOCK, 4, FillerBlockType.NATURAL_STONE);
			
			// Millstone Grit
			// todo: replace with correct block 
			AddOreSpawn(biome, 9,72,0,120,RegistryHandler.MILLSTONEGRIT_BLOCK, 62, NATURAL_STONE_OR_DIRT);
		}
	}
	
	public static void AddOreSpawn(Biome biome, int freq, int fromBottom, int fromSurface, int thickness, RegistryObject<Block> blockType, int veinSize, FillerBlockType filler)
	{
		if(filler == null)
		{
			filler = OreFeatureConfig.FillerBlockType.NATURAL_STONE; 
		}
		// freq, from-bottom, from-surface, thickness of band
		ConfiguredPlacement customConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(freq, fromBottom, fromSurface, thickness));
		OreFeatureConfig ofc = new OreFeatureConfig(filler,
				blockType.get().getDefaultState(), veinSize);
		biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
				Feature.ORE.withConfiguration(ofc).withPlacement(customConfig)); // 16 = vein size
	
	}
	
	private static FillerBlockType NATURAL_STONE_OR_DIRT = FillerBlockType.create("NATURAL_STONE_OR_DIRT", "natural_stone_or_dirt", (blockType) -> {
         if (blockType == null) {
            return false;
         } else {
            Block block = blockType.getBlock();
            return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE
            		|| block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.GRASS_BLOCK;
         }
      });
}

