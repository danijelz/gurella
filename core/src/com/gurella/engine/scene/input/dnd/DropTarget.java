package com.gurella.engine.scene.input.dnd;

import com.badlogic.gdx.utils.Array;

//TODO intersection info
public interface DropTarget {
	void dragIn(float screenX, float screenY, Array<Object> transferData);

	void dragMove(float screenX, float screenY, Array<Object> transferData);

	void dragOut(float screenX, float screenY, Array<Object> transferData);

	void drop(float screenX, float screenY, Array<Object> transferData);
}
