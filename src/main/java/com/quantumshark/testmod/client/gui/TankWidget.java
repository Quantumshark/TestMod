package com.quantumshark.testmod.client.gui;

public class TankWidget implements IScreenWidget {
	public TankWidget(int tankIndex, int left, int top, int width, int height) {
		this.tankIndex = tankIndex;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	
	private final int tankIndex;
	private final int left;
	private final int top;
	private final int width;
	private final int height;

	@Override
	public void render(GuiScreenBase<?> screen) {
		screen.drawTank(tankIndex, left, top, width, height);
	}
}
