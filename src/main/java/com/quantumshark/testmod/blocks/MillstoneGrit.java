package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class MillstoneGrit extends Block {
	
	public MillstoneGrit() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(5f, 9f)
				.sound(SoundType.STONE)
				.harvestLevel(2)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
