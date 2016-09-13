package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorScrolledComposite extends EditorLayoutComposite {
	boolean getAlwaysShowScrollBars();

	EditorControl getContent();

	boolean getExpandHorizontal();

	boolean getExpandVertical();

	int getMinHeight();

	int getMinWidth();

	GridPoint2 getOrigin();

	boolean getShowFocusedControl();

	void setAlwaysShowScrollBars(boolean show);

	void setContent(EditorControl content);

	void setExpandHorizontal(boolean expand);

	void setExpandVertical(boolean expand);

	void setMinHeight(int height);

	void setMinSize(int width, int height);

	void setMinWidth(int width);

	void setOrigin(int x, int y);

	void setShowFocusedControl(boolean show);

	void showControl(EditorControl control);

	public static class ScrolledCompositeStyle extends ScrollableStyle<ScrolledCompositeStyle> {
	}
}
