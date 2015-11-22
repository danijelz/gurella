package com.gurella.engine.graph.input;

import com.badlogic.gdx.math.Vector3;

public class IntersectionTouchEvent extends TouchEvent {
	public final Vector3 intersectionCoordinates = new Vector3();

	void set(int pointer, int button, int screenX, int screenY, Vector3 intersection) {
		super.set(pointer, button, screenX, screenY);
		intersectionCoordinates.set(intersection);
	}

	void set(int pointer, int button, int screenX, int screenY, PointerTrack pointerTrack, int index) {
		super.set(pointer, button, screenX, screenY);
		intersectionCoordinates.set(pointerTrack.getIntersectionX(index), pointerTrack.getIntersectionX(index),
				pointerTrack.getIntersectionX(index));
	}
}
