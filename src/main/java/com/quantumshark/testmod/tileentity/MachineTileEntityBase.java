package com.quantumshark.testmod.tileentity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.quantumshark.testmod.recipes.MachineInventoryRecipeWrapper;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.RecipeAndWrapper;
import com.quantumshark.testmod.recipes.RecipeComponent;
import com.quantumshark.testmod.utill.IItemDropper;
import com.quantumshark.testmod.utill.ISlotValidator;
import com.quantumshark.testmod.utill.MachineFluidHandler;
import com.quantumshark.testmod.utill.MachineItemHandler;
import com.quantumshark.testmod.utill.TankFluidHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

// base class for machines
public abstract class MachineTileEntityBase extends NameableTitleEntityBase
		implements ITickableTileEntity, ICapabilityProvider, ISlotValidator {
	// note: if you set these fields other than in constructor, expect to die.
	// can't make final as that doesn't allow assignment in child constructor
	// and can't pass into base constructor as that doesn't allow calling class
	// methods to calculate (?)
	// note: this is just for items. Do other types too
	protected MachineItemHandler inventory;
	protected MachineFluidHandler fluidInventory;
	
	protected int inputSlotCount;
	protected int outputSlotCount;
	protected int inputFluidSlotCount;
	protected int outputFluidSlotCount;

	public MachineTileEntityBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	protected abstract NonNullList<IRecipeType<MachineRecipeBase>> getRecipeTypes();

	public ItemStack getInputStack(int index) {
		if (index < 0 || index >= inputSlotCount) {
			return null;
		}
		return inventory.getStackInSlot(index);
	}

	public ItemStack getOutputStack(int index) {
		if (index < 0 || index >= outputSlotCount) {
			return null;
		}
		return inventory.getStackInSlot(inputSlotCount + index);
	}
	
	public FluidStack getInputFluidStack(int index) {
		if (index < 0 || index >= inputFluidSlotCount) {
			return null;
		}
		return fluidInventory.getFluidInTank(index);
	}

	public FluidStack getOutputFluidStack(int index) {
		if (index < 0 || index >= outputFluidSlotCount) {
			return null;
		}
		return fluidInventory.getFluidInTank(inputFluidSlotCount + index);
	}
	
	public TankFluidHandler getFluidTank(int slot ) {
		return fluidInventory.getTank(slot);
	}

	protected abstract MachineInventoryRecipeWrapper getInventoryWrapperForRecipe(MachineRecipeBase recipe);

	private static final String NBT_TAG_ITEM_INVENTORY = "Items";
	private static final String NBT_TAG_FLUID_INVENTORY = "Tanks";

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		CompoundNBT items = compound.getCompound(NBT_TAG_ITEM_INVENTORY);

		inventory.deserializeNBT(items);

		items = compound.getCompound(NBT_TAG_FLUID_INVENTORY);
		
		if(items != null) {
			fluidInventory.deserializeNBT(items);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		compound.put(NBT_TAG_ITEM_INVENTORY, inventory.serializeNBT());

		compound.put(NBT_TAG_FLUID_INVENTORY, fluidInventory.serializeNBT());
		
		return compound;
	}

	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
	}

	// note: for single type at least, cache this?
	private Set<MachineRecipeBase> findRecipes() {
		if (world == null) {
			return Collections.emptySet();
		}
		return world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> getRecipeTypes().contains(recipe.getType())).map(x -> (MachineRecipeBase) (x))
				.collect(Collectors.toSet());
	}

	@Nullable
	public RecipeAndWrapper findMatchingRecipe() {
		Set<MachineRecipeBase> recipes = findRecipes();
		for (MachineRecipeBase recipe : recipes) {
			MachineInventoryRecipeWrapper wrapper = getInventoryWrapperForRecipe(recipe);
			// note: this is going to be a bit tricky performance-wise in the generic
			// scenario, as we'd have to get a wrapper for each recipe in turn
			// single type version is a lot easier
			if (recipe.matches(wrapper, this.world)) {
				return new RecipeAndWrapper(recipe, wrapper);
			}
		}

		return null;
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		this.read(nbt);
	}

	@Override
	public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (inputSlotCount > 0 || outputSlotCount > 0)) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (inputFluidSlotCount > 0 || outputFluidSlotCount > 0)) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.fluidInventory));
		}
		return super.getCapability(cap, side);
	}

	// this is only used for items, hence the ItemStack signature.
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot < 0 || slot >= inputSlotCount) {
			// don't allow insertion into output slots.
			return false;
		}
		RecipeComponent inputWrapper = RecipeComponent.wrap(stack, "");

		Set<MachineRecipeBase> recipes = findRecipes();

		for (MachineRecipeBase recipe : recipes) {
			SlotWrapper[] inputsForRecipe = getInputSlots(recipe);
			for (int i = 0; i < inputsForRecipe.length; ++i) {
				SlotWrapper sw = inputsForRecipe[i];

				if (sw == null || !(sw instanceof SlotWrapperItem)) {
					continue;
				}

				SlotWrapperItem cast = (SlotWrapperItem) sw;
				if (cast.inventoryIndex != slot) {
					continue;
				}
				
				// so input i in the recipe goes in machine slot. Now just see if this matches it. 
				RecipeComponent ingredient = recipe.getInputs().get(i);

				if (ingredient.isFulfilledBy(inputWrapper, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public abstract SlotWrapper[] getInputSlots(MachineRecipeBase recipe);

	public abstract SlotWrapper[] getOutputSlots(MachineRecipeBase recipe);

	public abstract class SlotWrapper {
		protected SlotWrapper(String name) {
			this.name = name;
		}

		protected final String name;

		public abstract RecipeComponent getRecipeComponent();

		public abstract boolean insert(RecipeComponent output, boolean simulate);

		public abstract void decrease(RecipeComponent input);
	}

	public class SlotWrapperItem extends SlotWrapper {
		public SlotWrapperItem(int inventoryIndex, String name) {
			super(name);
			this.inventoryIndex = inventoryIndex;
		}

		public int inventoryIndex;

		public RecipeComponent getRecipeComponent() {
			return RecipeComponent.wrap(getInputStack(inventoryIndex), name);
		}

		@Override
		public boolean insert(RecipeComponent output, boolean simulate) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should be impossible)
			ItemStack outputStack = output.getAsItemStack().copy();
			
			return (ItemStack.EMPTY == inventory.insertOutputItem(inventoryIndex, outputStack, simulate));
		}

		@Override
		public void decrease(RecipeComponent input) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should be impossible)
			ItemStack inputStack = input.getAsItemStack();

			inventory.extractItem(inventoryIndex, inputStack.getCount(), false);
		}
	}

	public class SlotWrapperFluid extends SlotWrapper {
		private FluidAction toAction(boolean simulate) {
			return simulate?FluidAction.SIMULATE:FluidAction.EXECUTE;
		}
		
		public SlotWrapperFluid(int inventoryIndex, String name) {
			super(name);
			this.inventoryIndex = inventoryIndex;
		}

		public int inventoryIndex;

		public RecipeComponent getRecipeComponent() {
			return RecipeComponent.wrap(getInputFluidStack(inventoryIndex), name);
		}

		@Override
		public boolean insert(RecipeComponent output, boolean simulate) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should be impossible)
			FluidStack outputStack = output.getAsFluidStack().copy();
			
			return (outputStack.getAmount() == fluidInventory.getTank(inventoryIndex).fill(outputStack, toAction(simulate)));
		}

		@Override
		public void decrease(RecipeComponent input) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should be impossible)
			FluidStack inputStack = input.getAsFluidStack();

			fluidInventory.getTank(inventoryIndex).drain(inputStack.getAmount(), FluidAction.EXECUTE);
		}
	}
	
	protected class ItemDropper implements IItemDropper {
		@Override
		public void DropItem(ItemStack item) {
			ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), item.copy());
			world.addEntity(itemEntity);
		}
	}
}
