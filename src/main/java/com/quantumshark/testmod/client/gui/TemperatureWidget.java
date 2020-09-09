package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.capability.IHeatCapability;

public class TemperatureWidget implements IScreenWidget {
	public TemperatureWidget(int left, int top, IHeatCapability obj) {
		this.left = left;
		this.top = top;
		this.obj = obj;
	}

	private final int left;
	private final int top;
	private final IHeatCapability obj;

	@Override
	public void renderBackgroundLayer(GuiScreenBase<?> screen) {
		String text = String.format("%.2f", obj.getTemperatureK()) + "K";
		screen.drawWidgetShadowedString(text, left, top);
	}
}
