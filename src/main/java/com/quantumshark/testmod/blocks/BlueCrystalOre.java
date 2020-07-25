package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class BlueCrystalOre extends OreBlock {
	
	public BlueCrystalOre() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.4f, 5f)
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
