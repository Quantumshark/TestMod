package com.quantumshark.testmod.world.gen;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
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

			// Fluorite
			AddOreSpawn(biome, 12,2,5,200,RegistryHandler.FLUORITEORE_BLOCK, 1, FillerBlockType.NATURAL_STONE);
			
			// Oil Shale
			// todo: this should be more common and spawn in larger amounts in deserts and oceans
			AddOreSpawn(biome, 1,8,10,28,RegistryHandler.OILSHALE_BLOCK, 23, FillerBlockType.NATURAL_STONE);
			
			// Millstone Grit
			// todo: replace with correct block 
			AddOreSpawn(biome, 9,72,0,120,RegistryHandler.MILLSTONEGRIT_BLOCK, 62, NATURAL_STONE_OR_DIRT);
			
			// Limestone
			// todo: this should be much more common and spawn in larger amounts in oceans
			// note: there are many ocean biomes. This works out if this ocean has the "type" ocean, which hopefully they all do.
			if(BiomeDictionary.hasType(biome, Type.OCEAN)) {
				AddOreSpawn(biome, 10,12,0,120,RegistryHandler.LIMESTONE_BLOCK, 23, FillerBlockType.NATURAL_STONE);
			}
			if(biome == Biomes.DEEP_LUKEWARM_OCEAN) {
			}
			AddOreSpawn(biome, 7,12,0,120,RegistryHandler.LIMESTONE_BLOCK, 12, FillerBlockType.NATURAL_STONE);
		}
	}
	
	public static void AddOreSpawn(Biome biome, int freq, int fromBottom, int fromSurface, int thickness, RegistryObject<Block> blockType, int veinSize, FillerBlockType filler)
	{
		if(filler == null)
		{
			filler = OreFeatureConfig.FillerBlockType.NATURAL_STONE; 
		}
		// freq, from-bottom, from-surface, thickness of band
		ConfiguredPlacement<CountRangeConfig> customConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(freq, fromBottom, fromSurface, thickness));
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

