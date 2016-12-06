package com.gurella.engine.scene.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface Layout {
	GridPoint2 computeSize(Composite composite, int wHint, int hHint, boolean flushCache);

	void layout(Composite composite, boolean flushCache);
}
