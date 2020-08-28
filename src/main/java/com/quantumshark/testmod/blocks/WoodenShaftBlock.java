package com.quantumshark.testmod.blocks;

import javax.annotation.Nullable;

import com.quantumshark.testmod.tileentity.GrinderTileEntity;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

// todo: create a base class for all pipe-related things
public class WoodenShaftBlock extends Block {
	public static final BooleanProperty CONNECTED_UP = BlockStateProperties.UP;
	public static final BooleanProperty CONNECTED_DOWN = BlockStateProperties.DOWN;
	public static final BooleanProperty CONNECTED_NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty CONNECTED_SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty CONNECTED_EAST = BlockStateProperties.EAST;
	public static final BooleanProperty CONNECTED_WEST = BlockStateProperties.WEST;
	
	public WoodenShaftBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState());
	}
	

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(CONNECTED_UP, CONNECTED_DOWN, CONNECTED_NORTH,CONNECTED_SOUTH,CONNECTED_EAST,CONNECTED_WEST);
	}	
	
	protected static BooleanProperty getPropFromDir(Direction dir)
	{
		switch(dir)
		{
		case UP:
			return CONNECTED_UP;
		case DOWN:
			return CONNECTED_DOWN;
		case NORTH:
			return CONNECTED_NORTH;
		case SOUTH:
			return CONNECTED_SOUTH;
		case EAST:
			return CONNECTED_EAST;
		case WEST:
			return CONNECTED_WEST;
		}
		return null;
	}
	
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }
    
    // todo: how do we do this in 1.15.2? This is deprecated
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos,
            boolean isMoving) {
    	Direction updateDir = Direction.getFacingFromVector(fromPos.getX()-pos.getX(),fromPos.getY()-pos.getY(),fromPos.getZ()-pos.getZ());
    	BooleanProperty prop = getPropFromDir(updateDir);
		boolean current = state.get(prop);
		boolean newVal = canConnect(world, fromPos, updateDir);
		// don't update if no change!
		if(current != newVal)
		{
			BlockState newState = state.with(getPropFromDir(updateDir),  newVal);
			world.setBlockState(pos, newState);
		}

    	super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
    }
    

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RegistryHandler.WOODEN_SHAFT_TILE_ENTITY.get().create();
	}    
    
    public boolean canConnect(World world, BlockPos pos, Direction dir)
    {
		if (world != null) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile != null && tile instanceof ICapabilityProvider)
			{
				return (null != ((ICapabilityProvider)tile).getCapability(RegistryHandler.CAPABILITY_SHAFT_POWER, dir));
			}
		}
		return false;
    }
    
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState bs = getDefaultState();
		for (Direction face : Direction.values())
		{
			BlockPos relPos = context.getPos().offset(face);
			BlockState relBlockState =context.getWorld().getBlockState(relPos);
			BooleanProperty prop = getPropFromDir(face);
			boolean newVal = canConnect(context.getWorld(), relPos, face.getOpposite());
			bs = bs.with(prop, newVal);
		}
		return bs;
	}
}
