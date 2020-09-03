package com.quantumshark.testmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quantumshark.testmod.container.MachineContainerBase;
import com.quantumshark.testmod.utill.TankFluidHandler;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public abstract class GuiScreenBase<T extends MachineContainerBase<?>> extends ContainerScreen<T> {
	public GuiScreenBase(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);

		this.guiLeft = 0;	// these two get moved anyway
		this.guiTop = 0;
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
	}

	private static final int WIDTH = 176;
	private static final int HEIGHT = 166;

	// textures
	public abstract ResourceLocation getGuiTexture(); // e.g. grinder.png; 256x256

	public abstract ResourceLocation getBackgroundTexture(); // e.g. millstone_grit; 16x16

	private static final int PLAYER_INVENTORY_SLOTS = 36;
	private static final int INPUT_SLOT_SIZE = 18;
	private static final int OUTPUT_SLOT_SIZE = 26;
	private static final int ITEM_SIZE = 16;
	private static final int ARROW_HEIGHT = 17;
	private static final int ARROW_WIDTH = 26;
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int left = this.guiLeft;
		int top = this.guiTop;
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		// render background
		this.minecraft.getTextureManager().bindTexture(getBackgroundTexture());
		blit(left + 3, top + 3, 0, 0, WIDTH - 6, HEIGHT - 6, 16, 16);
		// render frame
		this.minecraft.getTextureManager().bindTexture(getGuiTexture());
		this.blit(left, top, 248, 0, 4, HEIGHT);
		this.blit(left + WIDTH - 4, top, 252, 0, 4, HEIGHT);
		this.blit(left + 4, top, 0, 250, WIDTH - 6, 3);
		this.blit(left + 4, top + HEIGHT - 3, 0, 253, WIDTH - 6, 3);
		// render inventory slots
		// render input and output slots
		int inputSlotCount = container.getTileEntity().getInputSlotCount();
		for(Slot s : container.inventorySlots) {
			int s1 = s.slotNumber;	// index in slots
			int s2 = s.getSlotIndex();	// only meaningful for inventory slots
			if(s1 < PLAYER_INVENTORY_SLOTS || s2 < inputSlotCount) {
				// render input slots
				this.blit(left+s.xPos-(INPUT_SLOT_SIZE-ITEM_SIZE)/2,  top+s.yPos-(INPUT_SLOT_SIZE-ITEM_SIZE)/2, 248-INPUT_SLOT_SIZE, 0, INPUT_SLOT_SIZE, INPUT_SLOT_SIZE);
			}
			else
			{
				// render output slots
				this.blit(left+s.xPos-(OUTPUT_SLOT_SIZE-ITEM_SIZE)/2,  top+s.yPos-(OUTPUT_SLOT_SIZE-ITEM_SIZE)/2, 248-OUTPUT_SLOT_SIZE, INPUT_SLOT_SIZE, OUTPUT_SLOT_SIZE, OUTPUT_SLOT_SIZE);
			}
		}
		
		// render widgets
		for(IScreenWidget w : container.screenWidgets) {
			w.render(this);
		}
	}
	
	public void renderProgessArrow(int left, int top, float progress) {
		this.minecraft.getTextureManager().bindTexture(getGuiTexture());
		// base one
		this.blit(this.guiLeft + left, this.guiTop + top, 176, ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT);
		// progress one
		int progressPixels = (int) (ARROW_WIDTH * progress);
		this.blit(this.guiLeft + left, this.guiTop + top, 176, 0, progressPixels, ARROW_HEIGHT);
	}

	public void drawTank(int tankIndex, int left, int top, int width, int height) {
		TankFluidHandler tank = container.getTileEntity().getFluidTank(tankIndex);

		FluidStack fluid = tank.getFluid();
		float fillPct = (float) tank.getFluidAmount() / (float) tank.getCapacity();

		if (fillPct == 0f) {
			return;
		}
		int scaledHeight = (int) (height * fillPct);
		int newTop = top + (height - scaledHeight);
		ResourceLocation rl = fluid.getFluid().getAttributes().getStillTexture();

		// for some reason this is missing the "textures" part of the path and the
		// extension.
		rl = new ResourceLocation(rl.getNamespace(), "textures/" + rl.getPath() + ".png");
		this.minecraft.getTextureManager().bindTexture(rl);
		blit(this.guiLeft + left, this.guiTop + newTop, 0, 0, width, scaledHeight);
//		blit(this.guiLeft+left, this.guiTop+newTop, 0, 0, width, scaledHeight, 16, 16);
//		blit(this.guiLeft+left, this.guiTop+newTop, width, scaledHeight, 0, 0, 16, 16, 16, 16);
//		int color = fluid.getFluid().getAttributes().getColor();
//		fill(this.guiLeft+left, this.guiTop+newTop, this.guiLeft+left+width, this.guiTop+newTop+scaledHeight, color);

		// Some blit param namings , thank you Mekanism
		// blit(int x, int y, int textureX, int textureY, int width, int height);
		// blit(int x, int y, TextureAtlasSprite icon, int width, int height);
		// blit(int x, int y, int textureX, int textureY, int width, int height, int
		// textureWidth, int textureHeight);
		// blit(int x, int y, int zLevel, float textureX, float textureY, int width, int
		// height, int textureWidth, int textureHeight);
		// blit(int x, int y, int desiredWidth, int desiredHeight, int textureX, int
		// textureY, int width, int height, int textureWidth, int textureHeight);
		// innerBlit(int x, int endX, int y, int endY, int zLevel, int width, int
		// height, float textureX, float textureY, int textureWidth, int textureHeight);
	}

	private static final int COLOR_FG = 0xf0f0f0;
	private static final int COLOR_SH = 0x101010;
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		drawShadowedString(this.title.getFormattedText(), 8.0f, 8.0f);
		drawShadowedString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, 69.0f);
	}

	private void drawShadowedString(String text, float x, float y) {
		drawShadowedString(text, x, y, COLOR_FG, COLOR_SH);
	}

	public void drawWidgetShadowedString(String text, float x, float y) {
		drawShadowedString(text, guiLeft+x, guiTop+y, COLOR_FG, COLOR_SH);
	}
	
	private void drawShadowedString(String text, float x, float y, int col_fg, int col_sh) {
		this.font.drawString(text, x+1, y+1, col_sh);
		this.font.drawString(text, x, y, col_fg);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
