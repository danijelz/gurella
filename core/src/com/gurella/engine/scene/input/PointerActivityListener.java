package com.gurella.engine.scene.input;

public interface PointerActivityListener {
	void onPointerActivity(int pointer, int button, PointerTrack pointerTrack);
	
	void reset();
}