package com.gurella.engine.editor.ui;

import com.gurella.engine.utils.GridRectangle;

public interface EditorScrollable extends EditorControl {
	GridRectangle getClientArea();

	EditorScrollBar getHorizontalBar();

	int getScrollbarsMode();

	EditorScrollBar getVerticalBar();

	public static class ScrollableStyle<T extends ScrollableStyle<T>> extends ControlStyle<T> {
		public boolean hScroll;
		public boolean vScroll;

		public T hScroll(boolean hScroll) {
			this.hScroll = hScroll;
			return cast();
		}

		public T vScroll(boolean vScroll) {
			this.vScroll = vScroll;
			return cast();
		}
	}
}
