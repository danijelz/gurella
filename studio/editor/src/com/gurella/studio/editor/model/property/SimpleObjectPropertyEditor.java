package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.Values;

public class SimpleObjectPropertyEditor<P> extends SimplePropertyEditor<P> {
	private Model<P> model;
	private Property<Object> delegateProperty;
	private PropertyEditor<Object> delegate;

	public SimpleObjectPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);
	}

	@Override
	protected void buildUi() {
		model = Models.getModel(getProperty().getType());
		delegateProperty = Values.cast(model.getProperties().get(0));

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);
		P value = getValue();
		delegate = createEditor(this, new PropertyEditorContext<>(context, model, value, delegateProperty));
		delegate.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	@Override
	public void present(Object modelInstance) {
		delegate.present(getValue());
	}
}
