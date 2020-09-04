package com.quantumshark.testmod.blocks.state;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;

public class LitStateHandler implements IBlockBehaviour {
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	
	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public BlockState setDefaultState(BlockState state) {
		return state.with(LIT, false);
	}
}
