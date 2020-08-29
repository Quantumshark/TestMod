package com.quantumshark.testmod.blocks;

import com.quantumshark.testmod.tileentity.ITileEntityLifetime;
import com.quantumshark.testmod.tileentity.NameableTitleEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

// base class for blocks that have an associated tile entity
// do I want to template this based on TE type?
public abstract class BlockWithTileEntityBase<T extends TileEntity> extends Block {

	public BlockWithTileEntityBase(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return getRegistry().get().create();
	}
	
	protected abstract RegistryObject<TileEntityType<T>> getRegistry();
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tile = worldIn.getTileEntity(pos);
		
		if (stack.hasDisplayName()) {
			if (tile instanceof NameableTitleEntityBase) {
				((NameableTitleEntityBase) tile).setCustomName(stack.getDisplayName());
			}
		}
		
		if(tile instanceof ITileEntityLifetime)
		{
			((ITileEntityLifetime) tile).onCreated(worldIn, pos, state, placer, stack);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);

		if(tile instanceof ITileEntityLifetime)
		{
			((ITileEntityLifetime) tile).onDestroyed(state, worldIn, pos, newState, isMoving);
		}

		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			worldIn.removeTileEntity(pos);
		}
		
		// need to call super after custom logic as we're tearing things apart
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}	
}
