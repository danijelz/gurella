package com.gurella.engine.editor.ui;

import com.gurella.engine.utils.GridRectangle;

public interface EditorScrollable extends EditorControl {
	GridRectangle computeTrim(int x, int y, int width, int height);

	GridRectangle getClientArea();

	EditorScrollBar getHorizontalBar();

	int getScrollbarsMode();

	EditorScrollBar getVerticalBar();
}
