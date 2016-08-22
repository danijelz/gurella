package com.gurella.engine.editor;

public interface EditorControl {
	EditorComposite getParent();

	boolean isDisposed();

	void dispose();

	<V> V getData(String key);

	void setData(String key, Object value);
}
