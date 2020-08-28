package com.quantumshark.testmod.tileentity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.quantumshark.testmod.recipes.GrinderRecipe;
import com.quantumshark.testmod.recipes.IMachineRecipe;
import com.quantumshark.testmod.utill.ExampleItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

// base class for machines
public abstract class MachineTileEntityBase extends NameableTitleEntityBase implements ITickableTileEntity {
	private ExampleItemHandler inventory;

	public MachineTileEntityBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);

		this.inventory = new ExampleItemHandler(getInputSlotCount() + getOutputSlotCount());
	}
	
	protected abstract IRecipeType<IMachineRecipe> getRecipeType();
	public abstract int getInputSlotCount();
	public abstract int getOutputSlotCount();
	
	public ItemStack getInputStack(int index) {
		if(index <0 || index >= getInputSlotCount())
		{
			return null;
		}
		return inventory.getStackInSlot(index);
	}
	
	public ItemStack getOutputStack(int index) {
		if(index <0 || index >= getOutputSlotCount())
		{
			return null;
		}
		return inventory.getStackInSlot(getInputSlotCount() + index);
	}

	protected ItemStack processRecipe(GrinderRecipe recipe) {
		// todo: make this handle more complex recipes.
		// use a recipe interface.
		ItemStack output = recipe.getRecipeOutput();
		this.inventory.insertItem(1, output.copy(), false);
		this.inventory.decrStackSize(0, 1);
	
		return output;
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
	
	private Set<IRecipe<?>> findRecipes() {
		return findRecipesByType(getRecipeType(), this.world);
	}
	
	@Nullable
	public GrinderRecipe getRecipe(ItemStack stack) {
		if (stack == null) {
			return null;
		}

		Set<IRecipe<?>> recipes = findRecipes();
		for (IRecipe<?> iRecipe : recipes) {
			GrinderRecipe recipe = (GrinderRecipe) iRecipe;
			if (recipe.matches(new RecipeWrapper(this.inventory), this.world)) {
				return recipe;
			}
		}

		return null;
	}
	
	public boolean hasRecipe(ItemStack stack) {
		if (stack == null) {
			return false;
		}
	
		Set<IRecipe<?>> recipes = findRecipes();
		for (IRecipe<?> iRecipe : recipes) {
			GrinderRecipe recipe = (GrinderRecipe) iRecipe;
			for(Ingredient ing : recipe.getIngredients()) {
				if(ing.test(stack)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn, World world) {
		if(world == null)
		{
			return Collections.emptySet();
		}
		Set<IRecipeType<?>> recipeTypes = world.getRecipeManager().getRecipes().stream().map(x->x.getType()).distinct().collect(Collectors.toSet());
		
		return world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet());
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static Set<IRecipe<?>> findRecipesByType(IRecipeType<?> typeIn) {
		ClientWorld world = Minecraft.getInstance().world;
		return world != null ? world.getRecipeManager().getRecipes().stream()
				.filter(recipe -> recipe.getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();
	}

	public static Set<ItemStack> getAllRecipeInputs(IRecipeType<?> typeIn, World worldIn) {
		Set<ItemStack> inputs = new HashSet<ItemStack>();
		Set<IRecipe<?>> recipes = findRecipesByType(typeIn, worldIn);
		for (IRecipe<?> recipe : recipes) {
			NonNullList<Ingredient> ingredients = recipe.getIngredients();
			ingredients.forEach(ingredient -> {
				for (ItemStack stack : ingredient.getMatchingStacks()) {
					inputs.add(stack);
				}
			});
		}
		return inputs;
	}
	
	// todo: make this very public.
	public static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if(stack1 == null || stack2 == null)
		{
			// can shove anything in an empty slot
			return true;
		}
		// check types, tags ...
		if(!ItemStack.areItemsEqual(stack1,  stack2))
		{
			return false;
		}
		// check for overflow
		return (stack1.getCount() + stack2.getCount() <= stack1.getMaxStackSize());
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
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
		}
		return super.getCapability(cap, side);
	}
}
