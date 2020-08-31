package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlastBricks extends Block {
	
	public BlastBricks() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(4.2f, 42f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
