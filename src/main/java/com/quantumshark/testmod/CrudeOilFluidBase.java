package com.quantumshark.testmod;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class CrudeOilFluidBase extends FlowingFluid {
	@Override
	public Fluid getFlowingFluid() {
		return RegistryHandler.CRUDE_OIL_FLUID_FLOWING.get();
	}

	@Override
	public Fluid getStillFluid() {
		return RegistryHandler.CRUDE_OIL_FLUID.get();
	}

	@Override
	protected boolean canSourcesMultiply() {
		// true for water, false for lava - infinite source flag.
		return false;
	}

	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
		// what happens when you replace this fluid with something else
	}

	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn) {
		// the slope you see when flowing. Apparently the standard is 4.
		return 4;
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
		// how much level decreases per block. Standard is 8 (i.e., how far it flows)
		return 3;
	}

	@Override
	public Item getFilledBucket() {
		return RegistryHandler.CRUDE_OIL_BUCKET.get();
	}

	@Override
	protected boolean canDisplace(IFluidState state, IBlockReader world, BlockPos pos,
			Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.isIn(RegistryHandler.CRUDE_OIL_TAG);
	}

	@Override
	public int getTickRate(IWorldReader worldReader) {
		// I think this is how many ticks until the block replicates. This one doesn't.
		return 60;
	}

	@Override
	protected float getExplosionResistance() {
		// whether it can be blown out of existence
		return 10.0f;
	}

	@Override
	protected BlockState getBlockState(IFluidState state) {
		return RegistryHandler.CRUDE_OIL_BLOCK.get().getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
	}

	@Override
	protected FluidAttributes createAttributes()
	{
		return FluidAttributes.builder(new ResourceLocation(TestMod.MOD_ID,"blocks/crude_oil_still"), new ResourceLocation(TestMod.MOD_ID,"blocks/crude_oil_flowing"))
			.translationKey("block."+TestMod.MOD_ID+".acid")
			.build(this);
	}
	
   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == RegistryHandler.CRUDE_OIL_FLUID.get() || fluidIn == RegistryHandler.CRUDE_OIL_FLUID_FLOWING.get();
   }	
	
	public static class Flowing extends CrudeOilFluidBase {

		@Override
		protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}
		
		@Override
		public boolean isSource(IFluidState state) {
			return false;
		}

		@Override
		public int getLevel(IFluidState state) {
			return state.get(CrudeOilFluidBase.LEVEL_1_8);
		}
	}
	
	public static class Source extends CrudeOilFluidBase {

		@Override
		public boolean isSource(IFluidState state) {
			return true;
		}

		@Override
		public int getLevel(IFluidState state) {
			return 8;
		}
	}
}
