package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlueCrystalBlock extends Block {
	
	public BlueCrystalBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.2f, 4f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
