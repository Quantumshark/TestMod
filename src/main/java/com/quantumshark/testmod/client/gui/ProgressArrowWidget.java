package com.quantumshark.testmod.client.gui;

import java.util.function.Supplier;

public class ProgressArrowWidget implements IScreenWidget {
	public ProgressArrowWidget(int left, int top, Supplier<Float> progress) {
		this.left = left;
		this.top = top;
		this.progress = progress;
	}

	private final int left;
	private final int top;
	private final Supplier<Float> progress;

	@Override
	public void render(GuiScreenBase<?> screen) {
		screen.renderProgessArrow(left, top, progress.get());
	}
}
