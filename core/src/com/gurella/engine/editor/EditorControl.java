package com.gurella.engine.editor;

import com.badlogic.gdx.graphics.Color;
import com.gurella.engine.editor.event.EditorEventListener;
import com.gurella.engine.editor.event.EditorEventType;

public interface EditorControl {
	EditorComposite getParent();

	void addListener(EditorEventType eventType, EditorEventListener listener);

	EditorEventListener[] getListeners(EditorEventType eventType);

	void removeListener(EditorEventType eventType, EditorEventListener listener);

	boolean isListening(EditorEventType eventType);

	boolean isDisposed();

	void dispose();

	<V> V getData(String key);

	void setData(String key, Object value);

	int getBorderWidth();

	boolean forceFocus();

	void redraw();

	void pack();

	void moveAbove(EditorControl control);

	void moveBelow(EditorControl control);

	Color getBackground();

	void setBackground(Color color);
}
