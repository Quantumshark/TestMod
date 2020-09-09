package com.quantumshark.testmod.blocks.state;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

// the idea of this interface is to provide a generic way that blocks can interact with blockstate properties
// e.g., one might contain all the code related to "Facing" for blocks that face NSEW
// one might contain all the code relating to "Lit" for blocks with an on/off animation
// constructor for specific block classes should add an instance of each property desired to a list in the generic base class
// and the generic base class's override methods will call each in turn
// that means we don't have to copy-and-paste so much code into each block class.
public interface IBlockBehaviour {
	// associate state with block type. called in fillStateContainer; call builder.add(state(s)) 
	default void fillStateContainer(Builder<Block, BlockState> builder) {}
	// set the default value of property in block state. called in constructor; return state.with(property, default value) 
	default BlockState setDefaultState(BlockState state) {return state;}
	// handlers for specific methods (override only if necessary, in general by returning state.with(property, value))
	default BlockState mirror(BlockState state, Mirror mirrorIn) {return state;}
	default BlockState rotate(BlockState state, Rotation rot) {return state;}
	default BlockState getStateForPlacement(BlockItemUseContext context, BlockState state) {return state;}
	default void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {}
	default int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side, int def) {return def;}
	default int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side, int def) {return def;}
	default boolean canProvidePower(BlockState state) {return false;}
	default boolean canConnectRedstone(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {return false;}
	default boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {return false;}
}
