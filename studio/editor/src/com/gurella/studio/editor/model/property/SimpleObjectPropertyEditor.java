package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.PropertyEditorFactory;
import com.gurella.studio.editor.model.SimplePropertyEditor;

public class SimpleObjectPropertyEditor<T> extends SimplePropertyEditor<T> {
	private PropertyEditor<?> delegate;
	private Model<T> model;
	private Property<?> delegateProperty;

	public SimpleObjectPropertyEditor(Composite parent, ModelPropertiesContainer<?> propertiesContainer,
			Property<T> property) {
		super(parent, propertiesContainer, property);
	}

	@Override
	protected void buildUi() {
		model = Models.getModel(property.getType());
		delegateProperty = model.getProperties().get(0);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		delegate = PropertyEditorFactory.createEditor(this, propertiesContainer, delegateProperty);
	}

	@Override
	public String getDescriptiveName() {
		return delegateProperty.getDescriptiveName();
	}

	@Override
	public void present(Object modelInstance) {
		delegate.present(getValue());
	}
}
