package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.studio.editor.GurellaEditor;
import com.gurella.studio.editor.GurellaStudioPlugin;
import com.gurella.studio.editor.model.ModelEditorContainer;

public abstract class PropertyEditor<T> extends Composite {
	protected PropertyEditorContext<T> context;
	protected ModelEditorContainer<?> propertiesContainer;
	protected Property<T> property;

	public PropertyEditor(Composite parent, PropertyEditorContext<T> context,
			ModelEditorContainer<?> propertiesContainer) {
		super(parent, SWT.NONE);
		this.context = context;
		this.propertiesContainer = propertiesContainer;
		this.property = context.property;
		GurellaStudioPlugin.getToolkit().adapt(this);
		buildUi();
		present(context.modelInstance);
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

	protected Object getModelInstance() {
		return context.modelInstance;
	}

	protected T getValue() {
		return property.getValue(context.modelInstance);
	}

	protected void setValue(T value) {
		property.setValue(context.modelInstance, value);
	}

	protected void setDirty() {
		getGurellaEditor().setDirty();
	}
}
