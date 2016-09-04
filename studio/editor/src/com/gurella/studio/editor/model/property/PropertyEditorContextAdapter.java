package com.gurella.studio.editor.model.property;

import com.gurella.engine.base.model.Property;

class PropertyEditorContextAdapter<P> implements com.gurella.engine.editor.property.PropertyEditorContext<P> {
	private PropertyEditorContext<?, P> context;
	private PropertyEditor<P> editor;

	public PropertyEditorContextAdapter(PropertyEditorContext<?, P> context, PropertyEditor<P> editor) {
		this.context = context;
		this.editor = editor;
	}

	@Override
	public Property<P> getProperty() {
		return context.property;
	}

	@Override
	public Object getModelInstance() {
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

	@Override
	public void addMenuItem(String text, Runnable action) {
		editor.addMenuItem(text, action);
	}

	@Override
	public void removeMenuItem(String text) {
		editor.removeMenuItem(text);
	}
}