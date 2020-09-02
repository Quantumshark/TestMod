package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class OilShale extends Block {
	
	public OilShale() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.1f, 3f)
				.sound(SoundType.STONE)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
