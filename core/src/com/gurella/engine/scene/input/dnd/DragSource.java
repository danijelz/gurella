package com.gurella.engine.scene.input.dnd;

//TODO intersection info
public interface DragSource {
	void dragStarted(float screenX, float screenY);
	
	void dragMove(float screenX, float screenY);
	
	void dragEnd(float screenX, float screenY); //TODO boolean canceled
	
	Object getTransferData(); //TODO unused
}
