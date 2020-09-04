package com.quantumshark.testmod.blocks.state;

import com.quantumshark.testmod.tileentity.MachineTileEntityBase;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

public class DropInventoryOnDestroy<T extends MachineTileEntityBase > implements IBlockBehaviour {
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof MachineTileEntityBase && state.getBlock() != newState.getBlock()) {
			@SuppressWarnings("unchecked")
			T cast = (T) tile;
			IItemHandlerModifiable inv = cast.getInventory();
			for(int i=0;i<inv.getSlots();++i) {
				ItemStack stack = inv.getStackInSlot((i));
				ItemEntity itemEntity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				worldIn.addEntity(itemEntity);
			}
		}
	}
}
