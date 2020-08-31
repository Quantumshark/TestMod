package com.quantumshark.testmod.items;

import com.quantumshark.testmod.TestMod;

import net.minecraft.item.Item;

public class ItemBase extends Item {

	public ItemBase() {
		super(new Item.Properties().group(TestMod.TAB));
	}
}
