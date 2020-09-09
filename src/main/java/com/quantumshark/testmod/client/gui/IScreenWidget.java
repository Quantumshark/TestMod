package com.quantumshark.testmod.client.gui;

public interface IScreenWidget {
	public default void renderBackgroundLayer(GuiScreenBase<?> screen) {
	}

	public default void renderForegroundLayer(GuiScreenBase<?> screen) {
	}

	public default void onInit(GuiScreenBase<?> guiScreenBase) {
	}
}
