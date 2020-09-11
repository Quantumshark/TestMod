package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.MachineContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BlastFurnaceScreen<T extends MachineContainerBase<?>> extends GuiScreenBase<T> {

	private static final ResourceLocation TEXTURE_GUI = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");
	private static final ResourceLocation TEXTURE_BACKGROUND = new ResourceLocation(TestMod.MOD_ID, "textures/gui/blast_bricks.png");

	public BlastFurnaceScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
