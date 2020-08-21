package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.recipes.GrinderRecipe;
import com.quantumshark.testmod.recipes.RecipeSerializer;
import com.quantumshark.testmod.recipes.IMachineRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeSerializerInit {

	public static final IRecipeSerializer<GrinderRecipe> EXAMPLE_RECIPE_SERIALIZER = new RecipeSerializer(new GrinderRecipe.RecipeFactory());
	public static final IRecipeType<IMachineRecipe> EXAMPLE_TYPE = registerType(GrinderRecipe.RECIPE_TYPE_ID);

	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(
			ForgeRegistries.RECIPE_SERIALIZERS, TestMod.MOD_ID);

	public static final RegistryObject<IRecipeSerializer<?>> EXAMPLE_SERIALIZER = RECIPE_SERIALIZERS.register("example",
			() -> EXAMPLE_RECIPE_SERIALIZER);

	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
		@Override
		public String toString() {
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	private static <T extends IRecipeType> T registerType(ResourceLocation recipeTypeId) {
		return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
	}
}
