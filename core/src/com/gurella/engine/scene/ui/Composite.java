package com.gurella.engine.scene.ui;

import com.gurella.engine.utils.ImmutableArray;

public interface Composite {
	ImmutableArray<UiComponent> components();

	void layout();

	UiComponent findComponentAt(int x, int y);
}
