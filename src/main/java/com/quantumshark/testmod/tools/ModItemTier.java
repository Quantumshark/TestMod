package com.quantumshark.testmod.tools;

import java.util.function.Supplier;

import com.quantumshark.testmod.utill.RegistryHandler;

import net.minecraft.item.Items;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public enum ModItemTier implements IItemTier {
	
	
	BLUECRYSTAL(1, 151, 5.0F, 1.2F, 20, () -> {
		return Ingredient.fromItems(RegistryHandler.BLUECRYSTAL.get());
	}),
	
	FLUORITE(1, 221, 5.5F, 1.7F, 15, () -> {
		return Ingredient.fromItems(RegistryHandler.FLUORITECRYSTAL.get());
	}),

	PHOTON(3, 5001, 12.0F, 5.6F, 2, () -> {
		return Ingredient.fromItems(Items.GLOWSTONE_DUST);
	});
	
	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final Supplier<Ingredient> repairMaterial;
	
	ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = repairMaterial;
	}
	
	@Override
	public int getMaxUses() {
		// TODO Auto-generated method stub
		return maxUses;
	}

	@Override
	public float getEfficiency() {
		// TODO Auto-generated method stub
		return efficiency;
	}

	@Override
	public float getAttackDamage() {
		// TODO Auto-generated method stub
		return attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		// TODO Auto-generated method stub
		return harvestLevel;
	}

	@Override
	public int getEnchantability() {
		// TODO Auto-generated method stub
		return enchantability;
	}

	@Override
	public Ingredient getRepairMaterial() {
		// TODO Auto-generated method stub
		return repairMaterial.get();
	}

}
