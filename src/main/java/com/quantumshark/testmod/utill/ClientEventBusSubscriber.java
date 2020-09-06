package com.quantumshark.testmod.utill;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.client.gui.BlastFurnaceScreen;
import com.quantumshark.testmod.client.gui.FlotationSeparatorScreen;
import com.quantumshark.testmod.client.gui.GrinderScreen;
import com.quantumshark.testmod.client.gui.SolidFuelHeaterScreen;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(RegistryHandler.GRINDER_CONTAINER.get(), GrinderScreen::new);
		ScreenManager.registerFactory(RegistryHandler.BLAST_FURNACE_CONTAINER.get(), BlastFurnaceScreen::new);
		ScreenManager.registerFactory(RegistryHandler.SOLID_FUEL_HEATER_CONTAINER.get(), SolidFuelHeaterScreen::new);
		ScreenManager.registerFactory(RegistryHandler.FLOTATION_SEPARATOR_CONTAINER.get(), FlotationSeparatorScreen::new);
		RenderTypeLookup.setRenderLayer(RegistryHandler.THERMALGLASS_BLOCK.get(), RenderType.getCutout());
	}
}
