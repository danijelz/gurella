package com.gurella.studio.editor.property;

import static com.gurella.studio.editor.property.PropertyEditorFactory.createEditor;

import java.util.Arrays;

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

	public SimpleObjectPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);

		model = Models.getModel(context.getPropertyType());
		delegateProperty = Values.cast(model.getProperties().get(0));

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);
		buildUi();
	}

	protected void buildUi() {
		P value = getValue();
		PropertyEditor<Object> delegate = createEditor(body,
				new PropertyEditorContext<>(context, model, value, delegateProperty));
		delegate.getComposite().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	private void rebuildUi() {
		Arrays.stream(body.getChildren()).forEach(c -> c.dispose());
		buildUi();
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
