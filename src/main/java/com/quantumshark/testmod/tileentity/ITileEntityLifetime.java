package com.quantumshark.testmod.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// interface to add to a tile entity that needs to do something when it's created and / or destroyed
public interface ITileEntityLifetime {
	default void onCreated(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {}
	default void onDestroyed(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {}
}
