package com.gurella.engine.scene.input;

public class TapInfo extends PointerInfo {
	public int count;
	
	@Override
	public void reset() {
		super.reset();
		count = 0;
	}
}
