package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class MillstoneGrit extends Block {
	
	public MillstoneGrit() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(2.4f, 9f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
