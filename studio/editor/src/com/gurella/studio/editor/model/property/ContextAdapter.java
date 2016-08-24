package com.gurella.studio.editor.model.property;

import com.gurella.engine.base.model.Property;

class ContextAdapter<P> implements com.gurella.engine.editor.property.PropertyEditorContext<P> {
	private PropertyEditorContext<?, P> context;

	public ContextAdapter(PropertyEditorContext<?, P> context) {
		this.context = context;
	}

	@Override
	public Property<P> getProperty() {
		return context.property;
	}

	@Override
	public Object getModel() {
		return context.model;
	}

	@Override
	public P getPropertyValue() {
		return context.getValue();
	}

	@Override
	public void setPropertyValue(P value) {
		context.setValue(value);
	}

}