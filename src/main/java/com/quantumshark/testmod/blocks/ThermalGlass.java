package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class ThermalGlass extends Block {
	
	public ThermalGlass() {
		super(Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(1.1f, 11f)
				.sound(SoundType.GLASS)
				.harvestLevel(1)
				.harvestTool(ToolType.PICKAXE)
				);
	}

}
