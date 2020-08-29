package com.quantumshark.testmod.tileentity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.quantumshark.testmod.recipes.IRecipeTemplate;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.utill.ISlotValidator;
import com.quantumshark.testmod.utill.MachineItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

// base class for machines
// todo: this currently hard-codes the recipe type.
// since this now knows what pattern of recipe it implements, it can be a lot more powerful
public abstract class MachineTileEntityBase<T extends IRecipeTemplate> extends NameableTitleEntityBase
		implements ITickableTileEntity, ICapabilityProvider, ISlotValidator {
	private MachineItemHandler inventory;
	private int inputSlotCount = getRecipeTemplate().getInputs().size();
	private int outputSlotCount = 1 + getRecipeTemplate().getSecondaryOutputs().size();

	public MachineTileEntityBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);

		this.inventory = new MachineItemHandler(inputSlotCount + outputSlotCount, this);
	}

	protected abstract IRecipeType<MachineRecipeBase<T>> getRecipeType();

	protected abstract T getRecipeTemplate();

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

	protected boolean processRecipe(MachineRecipeBase<T> recipe, boolean simulate) {
		NonNullList<ItemStack> otherOutputs = recipe.getRemainingItems(new RecipeWrapper(inventory));
		boolean ret = true;

		ItemStack output = recipe.getRecipeOutput();
		ret &= (ItemStack.EMPTY == this.inventory.insertOutputItem(inputSlotCount, output.copy(), simulate));
		for (int i = inputSlotCount + 1; i < inventory.getSlots(); ++i) {
			ret &= (ItemStack.EMPTY == this.inventory.insertOutputItem(i, otherOutputs.get(i).copy(), simulate));
		}
		if (!simulate) {
			for (int i = 0; i < inputSlotCount; ++i) {
				Ingredient ing = recipe.getIngredients().get(i);
				if (ing != Ingredient.EMPTY) {
					this.inventory.decrStackSize(i, 1);
				}

				// drop any left-over buckets
				// totally-untested-and-highly-dangerous
				ItemStack dropBucket = otherOutputs.get(i);
				if(dropBucket != null && dropBucket != ItemStack.EMPTY)
				{
					ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), dropBucket.copy());
					world.addEntity(itemEntity);
				}
			}
		}
		
		return ret;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);

		NonNullList<ItemStack> inv = NonNullList.<ItemStack>withSize(this.inventory.getSlots(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, inv);
		this.inventory.setNonNullList(inv);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		ItemStackHelper.saveAllItems(compound, this.inventory.toNonNullList());

		return compound;
	}

	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
	}

	private Set<MachineRecipeBase<T>> findRecipes() {
		IRecipeType<MachineRecipeBase<T>> typeIn = getRecipeType();
		if (world == null) {
			return Collections.emptySet();
		}
		
		return world.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == typeIn).map(x->(MachineRecipeBase<T>)(x))
				.collect(Collectors.toSet());
	}

	@Nullable
	public MachineRecipeBase<T> findMatchingRecipe() {
		Set<MachineRecipeBase<T>> recipes = findRecipes();
		for (MachineRecipeBase<T> recipe : recipes) {
			if (recipe.matches(new RecipeWrapper(this.inventory), this.world)) {
				return recipe;
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
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot < 0 || slot >= inputSlotCount) {
			// don't allow insertion into output slots.
			return false;
		}
		
		Set<MachineRecipeBase<T>> recipes = findRecipes();

		for(MachineRecipeBase<T> recipe : recipes)
		{
			if(recipe.getIngredients().get(slot).test(stack))
			{
				return true;
			}
		}
		return false;
	}
}
