package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class FluoriteOre extends OreBlock {
	
	public FluoriteOre() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.9f, 6f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}
	
//	@Override
//	public int getExpDrop(BlockState, IWorldReader, BlockPos, int fortune, int silktouch) {
//		return 1;
//	}

}
