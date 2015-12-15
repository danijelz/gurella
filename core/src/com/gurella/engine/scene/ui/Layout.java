package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.Vector2;

public interface Layout {
	Vector2 computeSize(Composite composite, int wHint, int hHint, boolean flushCache);

	void layout(Composite composite, boolean flushCache);
}
