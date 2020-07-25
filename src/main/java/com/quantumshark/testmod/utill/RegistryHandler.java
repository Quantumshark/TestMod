package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.armor.ModArmorMaterial;
import com.quantumshark.testmod.blocks.BlockItemBase;
import com.quantumshark.testmod.blocks.BlueCrystalBlock;
import com.quantumshark.testmod.blocks.BlueCrystalOre;
import com.quantumshark.testmod.items.ItemBase;
import com.quantumshark.testmod.tools.ModItemTier;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TestMod.MOD_ID);
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, TestMod.MOD_ID);
	
	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	// Blocks
	public static final RegistryObject<Block> BLUECRYSTALBLOCK_BLOCK = BLOCKS.register("blue_crystal_block", BlueCrystalBlock::new);
	public static final RegistryObject<Block> BLUECRYSTALORE_BLOCK = BLOCKS.register("blue_crystal_ore", BlueCrystalOre::new);
	
	
	// Block Items
	public static final RegistryObject<Item> BLUECRYSTALBLOCK_BLOCK_ITEM = ITEMS.register("blue_crystal_block", () -> new BlockItemBase(BLUECRYSTALBLOCK_BLOCK.get()));
	public static final RegistryObject<Item> BLUECRYSTALORE_BLOCK_ITEM = ITEMS.register("blue_crystal_ore", () -> new BlockItemBase(BLUECRYSTALORE_BLOCK.get()));
	
	
	// Items
	public static final RegistryObject<Item> BLUECRYSTAL = ITEMS.register("blue_crystal", ItemBase::new);
	
	
	// Tools
	public static final RegistryObject<SwordItem> BLUECRYSTALSWORD = ITEMS.register("blue_crystal_sword", () -> new SwordItem(ModItemTier.BLUECRYSTAL, 3, -2.4F, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<PickaxeItem> BLUECRYSTALPICKAXE = ITEMS.register("blue_crystal_pickaxe", () -> new PickaxeItem(ModItemTier.BLUECRYSTAL, 1, -2.8F, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<ShovelItem> BLUECRYSTALSHOVEL = ITEMS.register("blue_crystal_shovel", () -> new ShovelItem(ModItemTier.BLUECRYSTAL, 1.5F, -3F, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<AxeItem> BLUECRYSTALAXE = ITEMS.register("blue_crystal_axe", () -> new AxeItem(ModItemTier.BLUECRYSTAL, 6.8F, -3.18F, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<HoeItem> BLUECRYSTALHOE = ITEMS.register("blue_crystal_hoe", () -> new HoeItem(ModItemTier.BLUECRYSTAL, -1.8F, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<SwordItem> PHOTONBLADE = ITEMS.register("photon_blade", () -> new SwordItem(ModItemTier.PHOTON, 3, -2.4F, new Item.Properties().group(TestMod.TAB)));

	
	// Armor
	public static final RegistryObject<ArmorItem> BLUECRYSTALHELMET = ITEMS.register("blue_crystal_helmet", () -> new ArmorItem(ModArmorMaterial.BLUECRYSTAL, EquipmentSlotType.HEAD, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<ArmorItem> BLUECRYSTALCHESTPLATE = ITEMS.register("blue_crystal_chestplate", () -> new ArmorItem(ModArmorMaterial.BLUECRYSTAL, EquipmentSlotType.CHEST, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<ArmorItem> BLUECRYSTALLEGGINGS = ITEMS.register("blue_crystal_leggings", () -> new ArmorItem(ModArmorMaterial.BLUECRYSTAL, EquipmentSlotType.LEGS, new Item.Properties().group(TestMod.TAB)));
	public static final RegistryObject<ArmorItem> BLUECRYSTALBOOTS = ITEMS.register("blue_crystal_boots", () -> new ArmorItem(ModArmorMaterial.BLUECRYSTAL, EquipmentSlotType.FEET, new Item.Properties().group(TestMod.TAB)));
	
}
