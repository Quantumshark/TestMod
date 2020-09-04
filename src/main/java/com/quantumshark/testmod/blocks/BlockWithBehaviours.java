package com.quantumshark.testmod.blocks;

import com.quantumshark.testmod.blocks.state.DropInventoryOnDestroy;
import com.quantumshark.testmod.blocks.state.FacingStateHandler;
import com.quantumshark.testmod.blocks.state.IBlockBehaviour;
import com.quantumshark.testmod.blocks.state.LitStateHandler;
import com.quantumshark.testmod.tileentity.SolidFuelHeaterTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWithBehaviours extends Block {
	public BlockWithBehaviours(Properties properties) {
		super(properties);

		BlockState defaultState = getDefaultState();
		for (IBlockBehaviour handler : getBehaviours()) {
			defaultState = handler.setDefaultState(defaultState);
		}
		setDefaultState(defaultState);
	}
	
	public static final NonNullList<IBlockBehaviour> NONE = NonNullList.create();
	public static final NonNullList<IBlockBehaviour> LIT_FACING_DROPS = NonNullList.from(null, new LitStateHandler(), new FacingStateHandler(), new DropInventoryOnDestroy<SolidFuelHeaterTileEntity>());

	// override this in base classes by adding a few in.
	protected NonNullList<IBlockBehaviour> getBehaviours() {
		return NONE;
	}

	// note: we have an issue here as stateHandlers (child class member) is initialized 
	// after base class constructor (which calls this)
	// and before child class constructor
	// could get round this by just having getBehaviours() and making it return a static class variable?
	// since Blocks are kind of singletons, then it might be ok having a smaller number of static collections we refer to
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		for (IBlockBehaviour handler : getBehaviours()) {
			handler.fillStateContainer(builder);
		}
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		for (IBlockBehaviour handler : getBehaviours()) {
			state = handler.mirror(state, mirrorIn);
		}
		return state;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		for (IBlockBehaviour handler : getBehaviours()) {
			state = handler.rotate(state, rot);
		}
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = getDefaultState(); 
		for (IBlockBehaviour handler : getBehaviours()) {
			state = handler.getStateForPlacement(context, state);
		}
		return state;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		for (IBlockBehaviour handler : getBehaviours()) {
			handler.onReplaced(state, worldIn, pos, newState, isMoving);
		}

		// need to call super after custom logic as we're tearing things apart
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
}
