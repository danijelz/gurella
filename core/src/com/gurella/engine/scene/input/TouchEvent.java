package com.gurella.engine.scene.input;

public class TouchEvent {
	public int pointer;
	public int button;

	public int screenX;
	public int screenY;

	void set(int pointer, int button, int screenX, int screenY) {
		this.pointer = pointer;
		this.button = button;
		this.screenX = screenX;
		this.screenY = screenY;
	}
}
