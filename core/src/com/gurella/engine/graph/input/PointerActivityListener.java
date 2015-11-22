package com.gurella.engine.graph.input;

public interface PointerActivityListener {
	void onPointerActivity(int pointer, int button, PointerTrack pointerTrack);
	
	void reset();
}