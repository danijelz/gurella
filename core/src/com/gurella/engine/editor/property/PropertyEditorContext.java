package com.gurella.engine.editor.property;

import com.gurella.engine.base.model.Property;

public interface PropertyEditorContext<P> {
	Property<P> getProperty();

	Object getModelInstance();

	P getPropertyValue();

	void setPropertyValue(P value);

	void addMenuItem(String text, Runnable action);

	void removeMenuItem(String text);
}
