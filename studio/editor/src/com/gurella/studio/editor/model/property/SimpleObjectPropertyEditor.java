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
	public SimpleObjectPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		Model<P> model = Models.getModel(getProperty().getType());
		Property<Object> delegateProperty = Values.cast(model.getProperties().get(0));

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		P value = getValue();
		PropertyEditor<Object> delegate = createEditor(body, new PropertyEditorContext<>(context, model, value, delegateProperty));
		delegate.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}
}
