package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.GrinderContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

// Todo. make widgets (like tanks, progress arrows, etc) part of the container.
// this GUI could then just have a collection of them (polymorphic of course) and render them all.
// then rather than one screen class per machine, we could have one per tier, since the only differences are the textures used.
// and possibly the way that specific widgets are rendered ... 
// ... which begs the question of how different screen classes render different polymorphic widget classes differently!
public class GrinderScreen extends GuiScreenBase<GrinderContainer> {

	private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");
	private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(TestMod.MOD_ID, "textures/gui/millstone_grit.png");

	public GrinderScreen(GrinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	public ResourceLocation getGuiTexture() {
		// TODO Auto-generated method stub
		return TEXTURE_GUI;
	}

	@Override
	public ResourceLocation getBackgroundTexture() {
		// TODO Auto-generated method stub
		return TEXTURE_BACKGROUND;
	}
}
