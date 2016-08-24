package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;

public interface EditorWidget {
	void addListener(EditorEventType eventType, EditorEventListener listener);

	EditorEventListener[] getListeners(EditorEventType eventType);

	void removeListener(EditorEventType eventType, EditorEventListener listener);

	boolean isListening(EditorEventType eventType);

	boolean isDisposed();

	void dispose();

	<V> V getData(String key);

	void setData(String key, Object value);
	
	EditorUiFactory getUiFactory();
}
