package com.quantumshark.testmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.quantumshark.testmod.utill.RegistryHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("testmod-emirate-of-granada")
public class TestMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "testmod-emirate-of-granada";

    public TestMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void doClientStuff(final FMLClientSetupEvent event) 
    {

    }
    
    public static final ItemGroup TAB = new ItemGroup("TestTab") {
    
    	@Override
    	public ItemStack createIcon() {
    		return new ItemStack(RegistryHandler.BLUECRYSTAL.get());
    	}
    };
}
