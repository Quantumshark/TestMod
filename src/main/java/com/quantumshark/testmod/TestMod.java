package com.quantumshark.testmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.quantumshark.testmod.capability.HeatCapabilityProvider;
import com.quantumshark.testmod.capability.HeatCapabilityStorage;
import com.quantumshark.testmod.capability.IHeatCapability;
import com.quantumshark.testmod.capability.IShaftPower;
import com.quantumshark.testmod.capability.ShaftPowerDefImpl;
import com.quantumshark.testmod.capability.ShaftPowerStorage;
import com.quantumshark.testmod.packet.PacketHandler;
import com.quantumshark.testmod.utill.RegistryHandler;
import com.quantumshark.testmod.world.gen.ModOreGen;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("testmod-emirate-of-granada")
@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Bus.MOD)
public class TestMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "testmod-emirate-of-granada";

    public TestMod() {
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	
		
        // Register the setup method for modloading
    	modEventBus.addListener(this::setup);
        // Register the doClientStuff method for modloading
    	modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::commonSetup);

        RegistryHandler.init();
        PacketHandler.init();
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(FMLCommonSetupEvent event) 
    {
    	// try running this at different points to see when I can do it without crashing. I can't find any documentation to explain :(
		CapabilityManager.INSTANCE.register(IShaftPower.class, new ShaftPowerStorage(), ()->new ShaftPowerDefImpl());
		CapabilityManager.INSTANCE.register(IHeatCapability.class, new HeatCapabilityStorage(), ()->new HeatCapabilityProvider(1000,1,1));
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
    
    @SubscribeEvent
    public static void loadCompleteEvent(FMLLoadCompleteEvent event) {
    	ModOreGen.generateOres();
    }
}
