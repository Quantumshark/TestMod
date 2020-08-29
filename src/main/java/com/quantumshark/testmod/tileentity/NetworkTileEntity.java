package com.quantumshark.testmod.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkTileEntity extends TileEntity implements ITileEntityLifetime {
	
	private boolean isNew = true;
	private boolean isMaster = false;
	private NetworkTileEntity master = null;

	public NetworkTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
    @Override
    public void onCreated(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
    }	

    @Override
	public void onDestroyed(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    }
}
