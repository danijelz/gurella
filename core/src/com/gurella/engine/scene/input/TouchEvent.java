package com.gurella.engine.scene.input;

import com.badlogic.gdx.math.Vector2;

public class TouchEvent {
	public int pointer, button;
	public final Vector2 screenCoordinates = new Vector2();
	
	void set(int pointer, int button, int screenX, int screenY) {
		this.pointer = pointer;
		this.button = button;
		screenCoordinates.set(screenX, screenY);
	}
}
