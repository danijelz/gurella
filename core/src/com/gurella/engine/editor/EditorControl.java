package com.gurella.engine.editor;

import com.badlogic.gdx.graphics.Color;

public interface EditorControl {
	EditorComposite getParent();

	boolean isDisposed();

	void dispose();

	<V> V getData(String key);

	void setData(String key, Object value);

	boolean forceFocus();

	void redraw();

	void pack();

	void moveAbove(EditorControl control);

	void moveBelow(EditorControl control);

	Color getBackground();

	void setBackground(Color color);
}
