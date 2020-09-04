package com.quantumshark.testmod.blocks;

import java.util.Random;

import com.quantumshark.testmod.blocks.state.IBlockBehaviour;
import com.quantumshark.testmod.blocks.state.LitStateHandler;
import com.quantumshark.testmod.tileentity.SolidFuelHeaterTileEntity;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

// todo: push a lot of this to a base class for a block with a GUI.
// todo: push the stuff for dealing with LIT and FACING (and perhaps others) into mix-in classes with an interface we interact with.
public class SolidFuelHeaterBlock extends BlockWithGui<SolidFuelHeaterTileEntity> {

	public SolidFuelHeaterBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected RegistryObject<TileEntityType<SolidFuelHeaterTileEntity>> getRegistry() {
		return RegistryHandler.SOLID_FUEL_HEATER_TILE_ENTITY;
	}
	
	@Override
	protected NonNullList<IBlockBehaviour> getBehaviours() {
		return LIT_FACING_DROPS;
	}
	
	@Override
	public int getLightValue(BlockState state) {
		// light up the world when burning.
		
		return state.get(LitStateHandler.LIT) ? 10 : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.get(LitStateHandler.LIT)) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D) {
				worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F,
						false);
			}

		}
	}
}
