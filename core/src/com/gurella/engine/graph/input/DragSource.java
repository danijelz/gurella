package com.gurella.engine.graph.input;

public interface DragSource {
	void dragStarted(float screenX, float screenY);
	
	void dragMove(float screenX, float screenY);
	
	void dragEnd(float screenX, float screenY);
}
