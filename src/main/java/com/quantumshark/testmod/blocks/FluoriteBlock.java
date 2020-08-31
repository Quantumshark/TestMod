package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class FluoriteBlock extends Block {
	
	public FluoriteBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(2.8f, 6f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
