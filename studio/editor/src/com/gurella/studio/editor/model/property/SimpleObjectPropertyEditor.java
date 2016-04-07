package com.gurella.studio.editor.model.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.ModelEditorContainer;
import com.gurella.studio.editor.model.PropertyEditorFactory;

public class SimpleObjectPropertyEditor<T> extends SimplePropertyEditor<T> {
	private PropertyEditor<Object> delegate;
	private Model<T> model;
	private Property<Object> delegateProperty;

	public SimpleObjectPropertyEditor(Composite parent, PropertyEditorContext<T> context,
			ModelEditorContainer<?> propertiesContainer) {
		super(parent, context, propertiesContainer);
	}

	@Override
	protected void buildUi() {
		model = Models.getModel(property.getType());
		delegateProperty = Values.cast(model.getProperties().get(0));

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		delegate = PropertyEditorFactory.createEditor(this,
				new PropertyEditorContext<>(model, delegateProperty, getValue()), propertiesContainer);
		delegate.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void present(Object modelInstance) {
		delegate.present(getValue());
	}
}
