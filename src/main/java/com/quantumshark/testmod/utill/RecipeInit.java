package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
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
