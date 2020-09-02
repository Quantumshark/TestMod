package com.quantumshark.testmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.container.GrinderContainer;
import com.quantumshark.testmod.utill.TankFluidHandler;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class GrinderScreen extends ContainerScreen<GrinderContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(TestMod.MOD_ID, "textures/gui/grinder.png");

	public GrinderScreen(GrinderContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);

		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.minecraft.getTextureManager().bindTexture(TEXTURE);
		this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		this.blit(this.guiLeft + 79, this.guiTop + 35, 176, 0, this.container.getSmeltProgressionScaled(), 16);

		// draw the tank contents
		// todo: move to base class?
		drawTank(0, 134, 31, 20, 24);
	}

	private void drawTank(int tankIndex, int left, int top, int width, int height) {
		TankFluidHandler tank = container.getTileEntity().getFluidTank(tankIndex);

		FluidStack fluid = tank.getFluid();
		float fillPct = (float) tank.getFluidAmount() / (float) tank.getCapacity();

		if (fillPct == 0f) {
			return;
		}
		int scaledHeight = (int) (height * fillPct);
		int newTop = top + (height-scaledHeight);
		ResourceLocation rl = fluid.getFluid().getAttributes().getStillTexture();

		// for some reason this is missing the "textures" part of the path and the extension.
		rl = new ResourceLocation(rl.getNamespace(), "textures/" + rl.getPath() + ".png");
		this.minecraft.getTextureManager().bindTexture(rl);
		blit(this.guiLeft+left, this.guiTop+newTop, 0, 0, width, scaledHeight);
//		blit(this.guiLeft+left, this.guiTop+newTop, 0, 0, width, scaledHeight, 16, 16);
//		blit(this.guiLeft+left, this.guiTop+newTop, width, scaledHeight, 0, 0, 16, 16, 16, 16);
//		int color = fluid.getFluid().getAttributes().getColor();
//		fill(this.guiLeft+left, this.guiTop+newTop, this.guiLeft+left+width, this.guiTop+newTop+scaledHeight, color);
		
	    //Some blit param namings , thank you Mekanism
	    //blit(int x, int y, int textureX, int textureY, int width, int height);
	    //blit(int x, int y, TextureAtlasSprite icon, int width, int height);
	    //blit(int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight);
	    //blit(int x, int y, int zLevel, float textureX, float textureY, int width, int height, int textureWidth, int textureHeight);
	    //blit(int x, int y, int desiredWidth, int desiredHeight, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight);
	    //innerBlit(int x, int endX, int y, int endY, int zLevel, int width, int height, float textureX, float textureY, int textureWidth, int textureHeight);		
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(this.title.getFormattedText(), 8.0f, 8.0f, 0x404040);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, 69.0f, 0x404040);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
