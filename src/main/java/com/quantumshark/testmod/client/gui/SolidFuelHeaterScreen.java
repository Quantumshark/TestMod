package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.SolidFuelHeaterContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SolidFuelHeaterScreen extends GuiScreenBase<SolidFuelHeaterContainer> {

	private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");
	private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(TestMod.MOD_ID, "textures/gui/blast_bricks.png");

	public SolidFuelHeaterScreen(SolidFuelHeaterContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
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
