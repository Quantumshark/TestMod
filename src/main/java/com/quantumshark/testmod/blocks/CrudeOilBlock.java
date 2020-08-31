package com.quantumshark.testmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import com.quantumshark.testmod.utill.RegistryHandler;

public class CrudeOilBlock extends FlowingFluidBlock {

	public CrudeOilBlock() {
		super(() -> RegistryHandler.CRUDE_OIL_FLUID.get(), Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(10.0f).noDrops());
	}
	
	// todo: onEntityCollision slow the person down?
}
