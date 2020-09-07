package com.quantumshark.testmod.recipes;

import com.google.gson.JsonObject;
import com.quantumshark.testmod.recipes.RecipeTemplate.ComponentType;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

// note: this could be very similar to a generic machine inventory slot?
public class RecipeComponent {
	protected final String name;

	public RecipeComponent(String name) {
		this.name = name;
	}

	private RecipeComponent() {
		name = "";
	}

	public void write(PacketBuffer buffer) {
	}

	public void read(JsonObject json) {
	}

	public void read(PacketBuffer buffer) {
	}

	public static final RecipeComponent NONE = new RecipeComponent();

	public static RecipeComponent create(ComponentType componentType, String name) {
		switch (componentType) {
		case Item:
			return new RecipeComponentItem(name);
		case Fluid:
			return new RecipeComponentFluid(name);
		case None:
		default:
			return new RecipeComponent();
		}
	}

	public static RecipeComponent wrap(ItemStack itemStack, String name) {
		return new RecipeComponentItem(itemStack, name);
	}

	public static RecipeComponent wrap(FluidStack fluidStack, String name) {
		return new RecipeComponentFluid(fluidStack, name);
	}

	public ItemStack getAsItemStack() {
		return null;
	}

	public FluidStack getAsFluidStack() {
		return null;
	}

	private static class RecipeComponentItem extends RecipeComponent {
		public RecipeComponentItem(String name) {
			this(ItemStack.EMPTY, name);
		}

		public RecipeComponentItem(ItemStack itemStack, String name) {
			super(name);
			this.itemStack = itemStack;
		}

		private ItemStack itemStack;

		@Override
		public void read(JsonObject json) {
			itemStack = RecipeSerializer.getItemStack(json, name, true);
		}

		@Override
		public void read(PacketBuffer buffer) {
			itemStack = buffer.readItemStack();
		}

		@Override
		public void write(PacketBuffer buffer) {
			buffer.writeItemStack(itemStack, false);
		}

		@Override
		public boolean isFulfilledBy(RecipeComponent inventorySlot, boolean matchNone) {
			if (itemStack == ItemStack.EMPTY) {
				return matchNone;
			}
			if (!(inventorySlot instanceof RecipeComponentItem)) {
				return false;
			}
			RecipeComponentItem cast = (RecipeComponentItem) inventorySlot;

			ItemStack inventoryStack = cast.itemStack;

			if (inventoryStack.getItem() != itemStack.getItem()) {
				return false;
			}

			// verify tags
			CompoundNBT tags = itemStack.getTag();
			if (tags != null) {
				if(tags.contains("tag", 10))
				{
					tags = tags.getCompound("tag");
				}
				CompoundNBT inputTags = inventoryStack.getTag();
				if(inputTags != null && inputTags.contains("tag", 10))
				{
					inputTags = inputTags.getCompound("tag");
				}
		
				for (String key : tags.keySet()) {
					INBT matchVal = tags.get(key);
					if (inputTags == null || !inputTags.contains(key)) {
						switch(matchVal.getId())
						{
							case 1:
								byte v1 = tags.getByte(key);
								if(v1 != 0)
								{
									return false;
								}
								break;
							case 3:
								int v3 = tags.getInt(key);
								if(v3 != 0)
								{
									return false;
								}
								break;
							case 10:
								CompoundNBT v10 = tags.getCompound(key);
								if(v10 != null)
								{
									return false;
								}
								break;
							default:
								return false;
						}
						// todo: if matchVal is not empty / default value, return false;
					} else {
						// return whether inputTags.get(key) matches matchVal
						if(!inputTags.get(key).equals(matchVal)) {
							return false;
						}
					}
				}
			}

			return inventoryStack.getCount() >= itemStack.getCount();
		}

		@Override
		public ItemStack getAsItemStack() {
			return itemStack;
		}
	}

	private static class RecipeComponentFluid extends RecipeComponent {
		public RecipeComponentFluid(String name) {
			this(FluidStack.EMPTY, name);
		}

		public RecipeComponentFluid(FluidStack fluidStack, String name) {
			super(name);
			this.fluidStack = fluidStack;
		}

		private FluidStack fluidStack;

		@Override
		public void read(JsonObject json) {
			fluidStack = RecipeSerializer.getFluidStack(json, name, true);
		}

		@Override
		public void read(PacketBuffer buffer) {
			fluidStack = buffer.readFluidStack();
		}

		@Override
		public void write(PacketBuffer buffer) {
			buffer.writeFluidStack(fluidStack);
		}

		@Override
		public boolean isFulfilledBy(RecipeComponent inventorySlot, boolean matchNone) {
			if (fluidStack == FluidStack.EMPTY) {
				return matchNone;
			}
			if (!(inventorySlot instanceof RecipeComponentFluid)) {
				return false;
			}
			RecipeComponentFluid cast = (RecipeComponentFluid) inventorySlot;

			FluidStack inventoryStack = cast.fluidStack;

			if (inventoryStack.getFluid() != fluidStack.getFluid()) {
				return false;
			}

			return inventoryStack.getAmount() >= fluidStack.getAmount();
		}

		@Override
		public FluidStack getAsFluidStack() {
			return fluidStack;
		}
	}

	// matchNone true means that if the recipe doesn't require this component, we
	// return true (i.e., allow crafting).
	// if false, then return false (i.e., a recipe with nothing in the slot doesn't
	// mean you can put the item in the slot)
	public boolean isFulfilledBy(RecipeComponent inventorySlot, boolean matchNone) {
		return matchNone;
	}
}