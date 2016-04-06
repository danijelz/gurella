package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaEditor;

public abstract class PropertyEditor<T> extends Composite {
	protected ModelPropertiesContainer<?> propertiesContainer;
	protected Property<T> property;

	public PropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property) {
		super(parent, SWT.NONE);
		this.propertiesContainer = propertiesContainer;
		this.property = property;
		getGurellaEditor().getToolkit().adapt(this);
		buildUi();
		present(propertiesContainer.modelInstance);
		layout(true, true);
	}
	
	public String getDescriptiveName() {
		return property.getDescriptiveName();
	}

	protected abstract void buildUi();

	public abstract void present(Object modelInstance);

	protected GurellaEditor getGurellaEditor() {
		return propertiesContainer.editor;
	}

	protected Model<?> getModel() {
		return propertiesContainer.model;
	}

	protected Object getModelInstance() {
		return propertiesContainer.modelInstance;
	}

	protected T getValue() {
		return property.getValue(propertiesContainer.modelInstance);
	}

	protected void setValue(T value) {
		property.setValue(propertiesContainer.modelInstance, value);
	}

	protected void setDirty() {
		getGurellaEditor().setDirty();
	}

	protected FormToolkit getToolkit() {
		return getGurellaEditor().getToolkit();
	}
}
