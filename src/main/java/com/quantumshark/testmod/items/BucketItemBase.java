package com.quantumshark.testmod.items;

import com.quantumshark.testmod.TestMod;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;

public class BucketItemBase extends BucketItem {

	public BucketItemBase(java.util.function.Supplier<? extends Fluid> supplier, Item.Properties builder) {
		super(supplier, builder.group(TestMod.TAB));
	}
}
