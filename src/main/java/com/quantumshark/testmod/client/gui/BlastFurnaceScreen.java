package com.quantumshark.testmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.BlastFurnaceContainer;
import com.quantumshark.testmod.container.GrinderContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BlastFurnaceScreen extends GuiScreenBase<BlastFurnaceContainer> {

	private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");
	private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(TestMod.MOD_ID, "textures/gui/millstone_grit.png");

	public BlastFurnaceScreen(BlastFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		renderProgessArrow(69, 33, this.container.getSmeltProgressionScaled());
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
