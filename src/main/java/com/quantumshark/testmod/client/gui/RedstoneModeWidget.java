package com.quantumshark.testmod.client.gui;

import com.quantumshark.testmod.TestMod;
import com.quantumshark.testmod.tileentity.MachineTileEntityBase;

import net.minecraft.util.ResourceLocation;

public class RedstoneModeWidget implements IScreenWidget {
	public RedstoneModeWidget(int left, int top, MachineTileEntityBase obj) {
		this.left = left;
		this.top = top;
		this.obj = obj;
	}

	private final int left;
	private final int top;
	private final MachineTileEntityBase obj;

	@Override
	public void renderForegroundLayer(GuiScreenBase<?> screen) {
		ResourceLocation texture;
		switch (obj.getRedstoneMode()) {
		case EMIT:
			texture = new ResourceLocation(TestMod.MOD_ID, "textures/gui/redstone_mode_emit.png");
			break;
		case HIGH:
			texture = new ResourceLocation(TestMod.MOD_ID, "textures/gui/redstone_mode_high.png");
			break;
		case LOW:
			texture = new ResourceLocation(TestMod.MOD_ID, "textures/gui/redstone_mode_low.png");
			break;
		case NONE:
			texture = new ResourceLocation(TestMod.MOD_ID, "textures/gui/redstone_mode_none.png");
			break;
		default:
			return;
		}

		screen.drawIcon(texture, left, top, 0, 0, 16, 16, 16, 16);
	}

	@Override
	public void onInit(GuiScreenBase<?> screen) {
		screen.addButtonPlease(left, top, 16, 16, "",
//		screen.addButtonPlease(left-2, top-2, 20, 20, "", 

				(button) -> {
					obj.nextRedstoneMode();
				});
	}
}
