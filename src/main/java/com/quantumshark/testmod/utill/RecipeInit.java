package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.recipes.BlastFurnaceRecipe;
import com.quantumshark.testmod.recipes.CatalyticCrackingChamberRecipe;
import com.quantumshark.testmod.recipes.FlotationSeparatorRecipe;
import com.quantumshark.testmod.recipes.FractionalDistillationChamberRecipe;
import com.quantumshark.testmod.recipes.GrinderRecipe;
import com.quantumshark.testmod.recipes.RecipeSerializer;
import com.quantumshark.testmod.recipes.ThermalCrackingChamberRecipe;
import com.quantumshark.testmod.recipes.MachineRecipeBase;
import com.quantumshark.testmod.recipes.PolymerisationChamberRecipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeInit {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, TestMod.MOD_ID);

	// grinder recipes
	public static final IRecipeSerializer<GrinderRecipe> GRINDER_RECIPE_SERIALIZER_INST = new RecipeSerializer<GrinderRecipe>(new GrinderRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> GRINDER_RECIPE_TYPE = registerType(GrinderRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> GRINDER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("grinder", () -> GRINDER_RECIPE_SERIALIZER_INST);

	// blast furnace recipes
	public static final IRecipeSerializer<BlastFurnaceRecipe> BLAST_FURNACE_RECIPE_SERIALIZER_INST = new RecipeSerializer<BlastFurnaceRecipe>(new BlastFurnaceRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> BLAST_FURNACE_RECIPE_TYPE = registerType(BlastFurnaceRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> BLAST_FURNACE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("blast_furnace", () -> BLAST_FURNACE_RECIPE_SERIALIZER_INST);
	
	// flotation separator recipes
	public static final IRecipeSerializer<FlotationSeparatorRecipe> FLOTATION_SEPARATOR_RECIPE_SERIALIZER_INST = new RecipeSerializer<FlotationSeparatorRecipe>(new FlotationSeparatorRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> FLOTATION_SEPARATOR_RECIPE_TYPE = registerType(FlotationSeparatorRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> FLOTATION_SEPARATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("flotation_separator", () -> FLOTATION_SEPARATOR_RECIPE_SERIALIZER_INST);

	// fractional distillation chamber recipes
	public static final IRecipeSerializer<FractionalDistillationChamberRecipe> FRACTIONAL_DISTILLATION_CHAMBER_RECIPE_SERIALIZER_INST = new RecipeSerializer<FractionalDistillationChamberRecipe>(new FractionalDistillationChamberRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> FRACTIONAL_DISTILLATION_CHAMBER_RECIPE_TYPE = registerType(FractionalDistillationChamberRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> FRACTIONAL_DISTILLATION_CHAMBER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("fractional_distillation_chamber", () -> FRACTIONAL_DISTILLATION_CHAMBER_RECIPE_SERIALIZER_INST);

	// thermal cracking chamber recipes
	public static final IRecipeSerializer<ThermalCrackingChamberRecipe> THERMAL_CRACKING_CHAMBER_RECIPE_SERIALIZER_INST = new RecipeSerializer<ThermalCrackingChamberRecipe>(new ThermalCrackingChamberRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> THERMAL_CRACKING_CHAMBER_RECIPE_TYPE = registerType(ThermalCrackingChamberRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> THERMAL_CRACKING_CHAMBER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("thermal_cracking_chamber", () -> THERMAL_CRACKING_CHAMBER_RECIPE_SERIALIZER_INST);

	// catalytic cracking chamber recipes
	public static final IRecipeSerializer<CatalyticCrackingChamberRecipe> CATALYTIC_CRACKING_CHAMBER_RECIPE_SERIALIZER_INST = new RecipeSerializer<CatalyticCrackingChamberRecipe>(new CatalyticCrackingChamberRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> CATALYTIC_CRACKING_CHAMBER_RECIPE_TYPE = registerType(CatalyticCrackingChamberRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> CATALYTIC_CRACKING_CHAMBER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("catalytic_cracking_chamber", () -> CATALYTIC_CRACKING_CHAMBER_RECIPE_SERIALIZER_INST);

	// polymerisation chamber recipes
	public static final IRecipeSerializer<PolymerisationChamberRecipe> POLYMERISATION_CHAMBER_RECIPE_SERIALIZER_INST = new RecipeSerializer<PolymerisationChamberRecipe>(new PolymerisationChamberRecipe.RecipeFactory());
	public static final IRecipeType<MachineRecipeBase> POLYMERISATION_CHAMBER_RECIPE_TYPE = registerType(PolymerisationChamberRecipe.RECIPE_TYPE_ID);
	public static final RegistryObject<IRecipeSerializer<?>> POLYMERISATION_CHAMBER_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("polymerisation_chamber", () -> POLYMERISATION_CHAMBER_RECIPE_SERIALIZER_INST);
	
	private static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
		@Override
		public String toString() {
			return Registry.RECIPE_TYPE.getKey(this).toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends IRecipeType<?>> T registerType(ResourceLocation recipeTypeId) {
		return (T) Registry.register(Registry.RECIPE_TYPE, recipeTypeId, new RecipeType<>());
	}
}
