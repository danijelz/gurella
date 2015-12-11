package com.gurella.engine.scene.input;

import com.badlogic.gdx.utils.Array;

public interface DropTarget {
	void dragIn(float screenX, float screenY, Array<DragSource> dragSources);
	
	void dragMove(float screenX, float screenY, Array<DragSource> dragSources);
	
	void dragOut(float screenX, float screenY, Array<DragSource> dragSources);
	
	void drop(float screenX, float screenY, Array<DragSource> dragSources);
}
