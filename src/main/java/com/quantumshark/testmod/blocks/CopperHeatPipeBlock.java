package com.quantumshark.testmod.blocks;

import com.quantumshark.testmod.tileentity.CopperHeatPipeTileEntity;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.RegistryObject;

public class CopperHeatPipeBlock extends ConnectingBlock<CopperHeatPipeTileEntity> {
	public CopperHeatPipeBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	protected RegistryObject<TileEntityType<CopperHeatPipeTileEntity>> getRegistry() {
		return RegistryHandler.COPPER_HEAT_PIPE_TILE_ENTITY;
	}	

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

	@Override
	protected Capability<?> getMatchCapability() {
		return RegistryHandler.CAPABILITY_HEAT;
	}
}
