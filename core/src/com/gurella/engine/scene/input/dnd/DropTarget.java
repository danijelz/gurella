package com.gurella.engine.scene.input.dnd;

import com.badlogic.gdx.utils.Array;

//TODO intersection info, replace dragSources with DragSource.transferData
public interface DropTarget {
	void dragIn(float screenX, float screenY, Array<DragSource> dragSources);
	
	void dragMove(float screenX, float screenY, Array<DragSource> dragSources);
	
	void dragOut(float screenX, float screenY, Array<DragSource> dragSources);
	
	void drop(float screenX, float screenY, Array<DragSource> dragSources);
}
