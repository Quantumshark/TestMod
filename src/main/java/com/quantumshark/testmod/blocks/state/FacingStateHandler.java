package com.quantumshark.testmod.blocks.state;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

// state handler for blocks which can face any point of the compass (but not up or down)
public class FacingStateHandler implements IBlockBehaviour {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public BlockState setDefaultState(BlockState state) {
		return state.with(FACING, Direction.NORTH);
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState getStateForPlacement(BlockItemUseContext context, BlockState state) {
		return state.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
}
