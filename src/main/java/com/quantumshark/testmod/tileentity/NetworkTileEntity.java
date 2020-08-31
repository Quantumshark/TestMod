package com.quantumshark.testmod.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkTileEntity extends TileEntity implements ITileEntityLifetime {
	
	private boolean isMaster = false;
	private NetworkTileEntity master = null;

	public NetworkTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	// todo: do we need to load / save entire network? How else do we handle being loaded?
	
    @Override
    public void onCreated(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
    }

    @Override
	public void onDestroyed(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    }
    
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

        isMaster = compound.getBoolean("isMaster");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putBoolean("isMaster", isMaster);

		return compound;
	}    
}
