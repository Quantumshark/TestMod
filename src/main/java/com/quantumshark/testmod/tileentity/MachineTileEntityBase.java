package com.quantumshark.testmod.tileentity;

import javax.annotation.Nullable;

import com.quantumshark.testmod.capability.HeatCapabilityProvider;
import com.quantumshark.testmod.capability.IHeatCapability;
import com.quantumshark.testmod.recipes.RecipeComponent;
import com.quantumshark.testmod.utill.IItemDropper;
import com.quantumshark.testmod.utill.ISlotValidator;
import com.quantumshark.testmod.utill.MachineFluidHandler;
import com.quantumshark.testmod.utill.MachineItemCapabilityHandler;
import com.quantumshark.testmod.utill.MachineItemHandler;
import com.quantumshark.testmod.utill.RegistryHandler;
import com.quantumshark.testmod.utill.TankFluidHandler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

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
	protected HeatCapabilityProvider heat = null; // null by default. Create in constructor if this item handles heat.

	protected int inputSlotCount;
	protected int catalystSlotCount;
	protected int outputSlotCount;
	protected int inputFluidSlotCount;
	protected int catalystFluidSlotCount;
	protected int outputFluidSlotCount;

	public MachineTileEntityBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public ItemStack getInputStack(int index) {
		if (index < 0 || index >= getInputSlotCount()) {
			return null;
		}
		return inventory.getStackInSlot(index);
	}

	public ItemStack getOutputStack(int index) {
		if (index < 0 || index >= outputSlotCount) {
			return null;
		}
		return inventory.getStackInSlot(getInputSlotCount() + index);
	}

	// if you call this on a tile with no (i.e., null) fluidInventory, or with slot
	// out of range, expect to die.
	public FluidStack getInputFluidStack(int index) {
		if (index < 0 || index >= inputFluidSlotCount) {
			return null;
		}
		return fluidInventory.getFluidInTank(index);
	}

	// if you call this on a tile with no (i.e., null) fluidInventory, or with slot
	// out of range, expect to die.
	public FluidStack getOutputFluidStack(int index) {
		if (index < 0 || index >= outputFluidSlotCount) {
			return null;
		}
		return fluidInventory.getFluidInTank(inputFluidSlotCount + index);
	}

	// if you call this on a tile with no (i.e., null) fluidInventory, or with slot
	// out of range, expect to die.
	public TankFluidHandler getFluidTank(int slot) {
		return fluidInventory.getTank(slot);
	}

	public static final String NBT_TAG_ITEM_INVENTORY = "Items";
	public static final String NBT_TAG_FLUID_INVENTORY = "Tanks";
	public static final String NBT_TAG_HEAT = "Heat";

	@Override
	public void tick() {

		if (this.world == null || this.world.isRemote) {
			return;
		}

		if (heat != null) {
			heat.tick(world, pos);
		}
		BlockState bs = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, bs, bs, 2);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		CompoundNBT compound2;
		if (inventory != null) {
			compound2 = compound.getCompound(NBT_TAG_ITEM_INVENTORY);

			inventory.deserializeNBT(compound2);
		}

		if (fluidInventory != null) {
			compound2 = compound.getCompound(NBT_TAG_FLUID_INVENTORY);

			if (compound2 != null) {
				fluidInventory.deserializeNBT(compound2);
			}
		}

		if (heat != null) {
			compound2 = compound.getCompound(NBT_TAG_HEAT);
			if (compound2 != null) {
				heat.deserializeNBT(compound2);
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		if (inventory != null) {
			compound.put(NBT_TAG_ITEM_INVENTORY, inventory.serializeNBT());
		}

		if (fluidInventory != null) {
			compound.put(NBT_TAG_FLUID_INVENTORY, fluidInventory.serializeNBT());
		}

		if (heat != null) {
			compound.put(NBT_TAG_HEAT, heat.serializeNBT());
		}

		return compound;
	}

	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
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
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (getInputSlotCount() > 0 || outputSlotCount > 0)) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional
					.of(() -> new MachineItemCapabilityHandler(this.inventory, inputSlotCount + catalystSlotCount)));
		}
		// note: this could be more complex. The capability seems to lack the idea of
		// slots
		// in fact this will crash because we don't recognize the methods it calls.
		// looks like we'll have to return a specific tank per side.
//		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (inputFluidSlotCount > 0 || outputFluidSlotCount > 0)) {
//			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.fluidInventory));
//		}
		if (cap == RegistryHandler.CAPABILITY_HEAT && heat != null) {
			return RegistryHandler.CAPABILITY_HEAT.orEmpty(cap, LazyOptional.of(() -> heat));
		}
		return super.getCapability(cap, side);
	}

	// if you call this on a tile with no (i.e., null) inventory or fluidInventory,
	// expect to die.
	protected boolean AttemptFillBucket(int emptySlot, int tankSlot, int fullSlot) {
		if (emptySlot >= inventory.getSlots() || fullSlot >= inventory.getSlots()
				|| tankSlot >= fluidInventory.getTanks()) {
			return false;
		}
		if (inventory.getStackInSlot(fullSlot) != ItemStack.EMPTY) {
			return false;
		}
		ItemStack empty = inventory.getStackInSlot(emptySlot);
		if (empty == ItemStack.EMPTY) {
			return false;
		}
		boolean wasSingleton = (empty.getCount() == 1);
		boolean success = false;
		TankFluidHandler tank = fluidInventory.getTank(tankSlot);
		ItemStack out = null;
		if (empty.getItem() == Items.BUCKET) {
			FluidActionResult ret = FluidUtil.tryFillContainer(empty, tank, FluidAttributes.BUCKET_VOLUME, null, true);
			success = ret.success;
			out = ret.getResult();
		} else {
			// todo: train only a certain amount per tick. Only push the container down if
			// it's full or the tank is empty.
			IFluidHandlerItem item = empty.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
					.orElse(null);

			FluidStack fs = FluidUtil.tryFluidTransfer(item, tank, tank.getFluidAmount(), true);
			success = (fs != null && fs != FluidStack.EMPTY);
			out = empty;
		}
		if (out != null && success) {
			inventory.setStackInSlot(fullSlot, out);
			if (wasSingleton) {
				inventory.setStackInSlot(emptySlot, ItemStack.EMPTY);
			} else {
				inventory.extractItem(emptySlot, 1, false);
			}
		}
		return success;
	}

	// if you call this on a tile with no (i.e., null) inventory or fluidInventory,
	// expect to die.
	protected boolean AttemptEmptyBucket(int inputFullSlot, int tankSlot, int outputEmptySlot) {
		if (inputFullSlot >= inventory.getSlots() || outputEmptySlot >= inventory.getSlots()
				|| tankSlot >= fluidInventory.getTanks()) {
			return false;
		}
		// todo: empties might conceivably stack
		if (inventory.getStackInSlot(outputEmptySlot) != ItemStack.EMPTY) {
			return false;
		}
		ItemStack inputStack = inventory.getStackInSlot(inputFullSlot);
		if (inputStack == ItemStack.EMPTY) {
			return false;
		}
		// fulls can't generally stack, but leave this in just in case.
		boolean wasSingleton = (inputStack.getCount() == 1);
		boolean success = false;
		TankFluidHandler tank = fluidInventory.getTank(tankSlot);
		ItemStack out = null;
		// try the proper container version first.
		// todo: drain only a certain amount per tick. Only push the container down if
		// it's full or the tank is empty.
		IFluidHandlerItem item = inputStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				.orElse(null);
		if (item != null) {
			FluidStack fs = FluidUtil.tryFluidTransfer(tank, item, tank.getCapacity() - tank.getFluidAmount(), true);
			success = (fs != null && fs != FluidStack.EMPTY);
			if (inputStack.getContainerItem() == null) {
				// tank - output the (now less full) tank
				out = inputStack;
			} else {
				// bucket - output the empty bucket
				out = item.getContainer();
			}
		}
		if (out != null && success) {
			inventory.setStackInSlot(outputEmptySlot, out);
			if (wasSingleton) {
				inventory.setStackInSlot(inputFullSlot, ItemStack.EMPTY);
			} else {
				inventory.extractItem(inputFullSlot, 1, false);
			}
		}
		return success;
	}

	public int getInputSlotCount() {
		return inputSlotCount;
	}

	public int getCatalystSlotCount() {
		return catalystSlotCount;
	}

	public IHeatCapability getHeat() {
		return heat;
	}

	public abstract class SlotWrapper {
		protected SlotWrapper(String name) {
			this.name = name;
		}

		protected final String name;

		public abstract RecipeComponent getRecipeComponent();

		public abstract boolean insert(RecipeComponent output, boolean simulate, SlotWrapper tagMergeSource);

		public abstract void decrease(RecipeComponent input);

		public abstract CompoundNBT getCurrentTag();
	}

	public class SlotWrapperItem extends SlotWrapper {
		public SlotWrapperItem(int inventoryIndex, String name) {
			super(name);
			this.inventoryIndex = inventoryIndex;
		}

		public int inventoryIndex;

		public RecipeComponent getRecipeComponent() {
			return RecipeComponent.wrap(inventory.getStackInSlot(inventoryIndex), name);
		}

		@Override
		public CompoundNBT getCurrentTag() {
			return inventory.getStackInSlot(inventoryIndex).getTag();
		}

		@Override
		public boolean insert(RecipeComponent output, boolean simulate, SlotWrapper tagMergeSource) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should
			// be impossible)
			ItemStack outputStack = output.getAsItemStack().copy();

			if (tagMergeSource != null) {
				CompoundNBT tag = outputStack.getTag();
				if (tag == null) {
					tag = new CompoundNBT();
				}
				tag.merge(tagMergeSource.getCurrentTag());
				outputStack.setTag(tag);
			}

			return (ItemStack.EMPTY == inventory.insertOutputItem(inventoryIndex, outputStack, simulate));
		}

		@Override
		public void decrease(RecipeComponent input) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should
			// be impossible)
			ItemStack inputStack = input.getAsItemStack();

			inventory.extractItem(inventoryIndex, inputStack.getCount(), false);
		}
	}

	public class SlotWrapperFluid extends SlotWrapper {
		private FluidAction toAction(boolean simulate) {
			return simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE;
		}

		public SlotWrapperFluid(int inventoryIndex, String name) {
			super(name);
			this.inventoryIndex = inventoryIndex;
		}

		public int inventoryIndex;

		public RecipeComponent getRecipeComponent() {
			return RecipeComponent.wrap(fluidInventory.getFluidInTank(inventoryIndex), name);
		}

		@Override
		public CompoundNBT getCurrentTag() {
			return fluidInventory.getFluidInTank(inventoryIndex).getTag();
		}

		@Override
		public boolean insert(RecipeComponent output, boolean simulate, SlotWrapper tagMergeSource) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should
			// be impossible)
			FluidStack outputStack = output.getAsFluidStack().copy();

			if (tagMergeSource != null) {
				CompoundNBT tag = outputStack.getTag();
				if (tag == null) {
					tag = new CompoundNBT();
				}
				tag.merge(tagMergeSource.getCurrentTag());
				outputStack.setTag(tag);
			}
			
			return (outputStack.getAmount() == fluidInventory.getTank(inventoryIndex).fill(outputStack,
					toAction(simulate)));
		}

		@Override
		public void decrease(RecipeComponent input) {
			// this is null if wrong type, hopefully we'll throw if that happens (it should
			// be impossible)
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
