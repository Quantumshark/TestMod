package com.quantumshark.testmod.tileentity;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.blocks.GrinderBlock;
import com.quantumshark.testmod.capability.ShaftPowerDefImpl;
import com.quantumshark.testmod.container.GrinderContainer;
import com.quantumshark.testmod.recipes.GrinderRecipe;
import com.quantumshark.testmod.recipes.IMachineRecipe;
import com.quantumshark.testmod.utill.RecipeInit;
import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

public class GrinderTileEntity extends MachineTileEntityBase {
	private ShaftPowerDefImpl shaft;

	public int currentSmeltTime;
	public final int maxSmeltTime = 100;

	public GrinderTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
		shaft = new ShaftPowerDefImpl();
	}

	public GrinderTileEntity() {
		this(RegistryHandler.GRINDER_TILE_ENTITY.get());
	}
	
	@Override
	public int getInputSlotCount() { return 1; }
	
	@Override
	public int getOutputSlotCount()  { return 1; }

	@Override
	public Container createMenu(final int windowID, final PlayerInventory playerInv, final PlayerEntity playerIn) {
		return new GrinderContainer(windowID, playerInv, this);
	}

	@Override
	public void tick() {
		boolean dirty = false;

		if (this.world != null && !this.world.isRemote) {
			if (this.world.isBlockPowered(this.getPos())) {
				ItemStack inputStack = getInputStack(0);
				// reset progress if you remove the input item. 
				if(inputStack == null || inputStack.isEmpty())
				{
					this.currentSmeltTime = 0;
					this.world.setBlockState(this.getPos(),
							this.getBlockState().with(GrinderBlock.LIT, false));
				}
				else
				{
					GrinderRecipe recipe =this.getRecipe(inputStack);
					if(recipe != null)
					{
						ItemStack outputStack = getOutputStack(0);
						if(outputStack == null || outputStack.isEmpty() || canCombine(outputStack, recipe.getRecipeOutput()))
						{
							if (this.currentSmeltTime != this.maxSmeltTime) {
								this.world.setBlockState(this.getPos(),
										this.getBlockState().with(GrinderBlock.LIT, true));
								this.currentSmeltTime++;
								dirty = true;
							} else {
								this.world.setBlockState(this.getPos(),
										this.getBlockState().with(GrinderBlock.LIT, false));
								this.currentSmeltTime = 0;
								
								ItemStack output = processRecipe(this.getRecipe(getInputStack(0)));
								
								dirty = true;
							}
						}
					}
				}
			}
			else
			{
				this.world.setBlockState(this.getPos(),
						this.getBlockState().with(GrinderBlock.LIT, false));
			}
		}

		if (dirty) {
			this.markDirty();
			this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(),
					Constants.BlockFlags.BLOCK_UPDATE);
		}
	}
	
	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + TestMod.MOD_ID + ".grinder");
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		this.currentSmeltTime = compound.getInt("CurrentSmeltTime");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("CurrentSmeltTime", this.currentSmeltTime);

		return compound;
	}
	
	@Override
	protected IRecipeType<IMachineRecipe> getRecipeType() {
		 return RecipeInit.GRINDER_RECIPE_TYPE;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == RegistryHandler.CAPABILITY_SHAFT_POWER)
		{
			if(side == Direction.UP)
			{
				return RegistryHandler.CAPABILITY_SHAFT_POWER.orEmpty(cap, LazyOptional.of(() -> this.shaft));
			}
			else return null;
		}
		return super.getCapability(cap, side);
	}	
}
