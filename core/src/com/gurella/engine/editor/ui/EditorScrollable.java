package com.gurella.engine.editor.ui;

import com.gurella.engine.utils.GridRectangle;

public interface EditorScrollable extends EditorControl {
	GridRectangle getClientArea();

	EditorScrollBar getHorizontalBar();

	int getScrollbarsMode();

	EditorScrollBar getVerticalBar();
}
