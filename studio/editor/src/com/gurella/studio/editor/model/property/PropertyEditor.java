package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;

public abstract class PropertyEditor<T> extends Composite {
	protected ModelPropertiesContainer<?> propertiesContainer;
	protected Property<T> property;
	protected Object modelInstance;

	public PropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer, Property<T> property,
			Object modelInstance) {
		super(parent, SWT.NONE);
		this.propertiesContainer = propertiesContainer;
		this.property = property;
		this.modelInstance = modelInstance;
		GurellaStudioPlugin.getToolkit().adapt(this);
		buildUi();
		present(modelInstance);
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
		return modelInstance;
	}

	protected T getValue() {
		return property.getValue(modelInstance);
	}

	protected void setValue(T value) {
		property.setValue(modelInstance, value);
	}

	protected void setDirty() {
		getGurellaEditor().setDirty();
	}
}
