package com.gurella.engine.scene.input;

public class TapInfo extends TouchInfo {
	public int count;
	
	@Override
	public void reset() {
		super.reset();
		count = 0;
	}
}
