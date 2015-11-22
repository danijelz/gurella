package com.gurella.engine.geometry;

import com.badlogic.gdx.math.Vector;

public interface Geometry<V extends Vector<V>> {
	Bounds<V> getBounds();

	boolean contains(V point);

	boolean intersects(Geometry<V> geometry);
}
