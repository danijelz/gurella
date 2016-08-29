package com.gurella.engine.editor.ui;

import com.badlogic.gdx.utils.Disposable;
import com.gurella.engine.editor.ui.event.EditorEventListener;
import com.gurella.engine.editor.ui.event.EditorEventType;

public interface EditorWidget extends Disposable {
	void addListener(EditorEventType eventType, EditorEventListener listener);

	EditorEventListener[] getListeners(EditorEventType eventType);

	void removeListener(EditorEventType eventType, EditorEventListener listener);

	boolean isListening(EditorEventType eventType);

	boolean isDisposed();

	<V> V getData(String key);

	void setData(String key, Object value);

	EditorUi getUiFactory();
}
