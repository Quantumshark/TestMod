package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.recipes.BlastFurnaceRecipe;
import com.quantumshark.testmod.recipes.FlotationSeparatorRecipe;
import com.quantumshark.testmod.recipes.GrinderRecipe;
import com.quantumshark.testmod.recipes.RecipeSerializer;
import com.quantumshark.testmod.recipes.MachineRecipeBase;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeInit {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(
			ForgeRegistries.RECIPE_SERIALIZERS, TestMod.MOD_ID);

	// grinder recipes
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_RECIPE_SERIALIZER_INST = new RecipeSerializer<GrinderRecipe>(new GrinderRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> GRINDER_RECIPE_TYPE = registerType(GrinderRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> GRINDER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("grinder",
			() -> GRINDER_RECIPE_SERIALIZER_INST);

	// blast furnace recipes
	public static final IRecipeSerializer<BlastFurnaceRecipe> BLAST_FURNACE_RECIPE_SERIALIZER_INST = new RecipeSerializer<BlastFurnaceRecipe>(new BlastFurnaceRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> BLAST_FURNACE_RECIPE_TYPE = registerType(BlastFurnaceRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> BLAST_FURNACE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("blast_furnace",
			() -> BLAST_FURNACE_RECIPE_SERIALIZER_INST);
	
	// flotation separator recipes
	public static final IRecipeSerializer<FlotationSeparatorRecipe> FLOTATION_SEPARATOR_RECIPE_SERIALIZER_INST = new RecipeSerializer<FlotationSeparatorRecipe>(new FlotationSeparatorRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> FLOTATION_SEPARATOR_RECIPE_TYPE = registerType(FlotationSeparatorRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> FLOTATION_SEPARATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("flotation_separator",
			() -> FLOTATION_SEPARATOR_RECIPE_SERIALIZER_INST);

	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
		@Override
		public String toString() {
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	private static <T extends IRecipeType<?>> T registerType(ResourceLocation recipeTypeId) {
		return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
	}
}
