package com.gurella.engine.editor.property;

import com.gurella.engine.base.model.Property;

public interface PropertyEditorContext<P> {
	Property<P> getProperty();

	Object getModel();

	P getPropertyValue();

	void setPropertyValue(P value);
}
