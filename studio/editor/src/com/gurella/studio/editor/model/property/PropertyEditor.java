package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.GurellaStudioPlugin;

public abstract class PropertyEditor<P> extends Composite {
	protected PropertyEditorContext<?, P> context;

	protected P cachedValue;

	public PropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, SWT.NONE);
		this.context = context;
		GurellaStudioPlugin.getToolkit().adapt(this);
		cachedValue = getValue();
	}

	public String getDescriptiveName() {
		return context.property.getDescriptiveName();
	}

	public Property<P> getProperty() {
		return context.property;
	}

	public P getCachedValue() {
		return cachedValue;
	}

	protected Object getModelInstance() {
		return context.modelInstance;
	}

	protected P getValue() {
		return context.property.getValue(context.modelInstance);
	}

	protected void setValue(P value) {
		if (!Values.isEqual(cachedValue, value)) {
			context.property.setValue(context.modelInstance, value);
			context.propertyValueChanged(cachedValue, value);
			cachedValue = value;
		}
	}
}
