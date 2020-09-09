package com.quantumshark.testmod.tileentity;

public enum RedstoneControlMode {
	NONE,	// always run
	LOW,	// run when no redstone input
	HIGH,	// run when redstone input
	EMIT	// emit redstone signal when running
}
