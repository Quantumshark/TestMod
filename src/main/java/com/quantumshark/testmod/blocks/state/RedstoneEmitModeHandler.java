package com.quantumshark.testmod.blocks.state;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase;
import com.quantumshark.testmod.tileentity.RedstoneControlMode;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class RedstoneEmitModeHandler extends LitStateHandler {
	private static RedstoneControlMode getMode(IBlockReader blockAccess, BlockPos pos) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if(te != null && te instanceof MachineTileEntityBase) {
			MachineTileEntityBase cast = (MachineTileEntityBase) te;
			return cast.getRedstoneMode();
		}
		return RedstoneControlMode.NONE;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side, int def) {
		if(blockState.get(LIT) && getMode(blockAccess, pos) == RedstoneControlMode.EMIT)
		{
			return 15;
		}
		return def;
	}
/*
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side, int def) {
		return def;
	}
*/
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public boolean canConnectRedstone(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return false;
	}
}
