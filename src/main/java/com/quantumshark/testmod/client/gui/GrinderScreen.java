package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.GrinderContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GrinderScreen extends GuiScreenBase<GrinderContainer> {

	private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");
	private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(TestMod.MOD_ID, "textures/gui/millstone_grit.png");

	public GrinderScreen(GrinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		renderProgessArrow(55, 33, this.container.getSmeltProgressionScaled());

		// draw the tank contents
		drawTank(0, 135, 29, 19, 24);
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
