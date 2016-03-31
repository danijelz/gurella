package com.gurella.studio.editor.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaEditor;

abstract class PropertyEditor<T> extends Composite {
	protected ModelPropertiesContainer<?> propertiesContainer;
	protected Property<T> property;

	public PropertyEditor(ModelPropertiesContainer<?> propertiesContainer, Property<T> property) {
		super(propertiesContainer.getBody(), SWT.BORDER);
		this.propertiesContainer = propertiesContainer;
		this.property = property;
		getGurellaEditor().getToolkit().adapt(this);
		buildUi();
		present(propertiesContainer.modelInstance);
		layout(true, true);
	}

	protected abstract void buildUi();

	protected abstract void present(Object modelInstance);

	protected GurellaEditor getGurellaEditor() {
		return propertiesContainer.editor;
	}

	protected Model<?> getModel() {
		return propertiesContainer.model;
	}

	protected Object getModelInstance() {
		return propertiesContainer.modelInstance;
	}

	protected void setDirty() {
		getGurellaEditor().setDirty();
	}

	protected FormToolkit getToolkit() {
		return getGurellaEditor().getToolkit();
	}
}
