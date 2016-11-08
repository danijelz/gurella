package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.Vector2;

public interface Layout {
	Vector2 computeSize(CompositeComponent compositeComponent, int wHint, int hHint, boolean flushCache);

	void layout(CompositeComponent compositeComponent, boolean flushCache);
}
