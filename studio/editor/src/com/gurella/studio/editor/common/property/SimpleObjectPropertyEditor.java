package com.gurella.studio.editor.common.property;

import static com.gurella.studio.editor.common.property.PropertyEditorFactory.createEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.metatype.Model;
import com.gurella.engine.metatype.Models;
import com.gurella.engine.metatype.Property;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.utils.UiUtils;

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
		content.setLayout(layout);
		buildUi();
	}

	protected void buildUi() {
		P value = getValue();
		PropertyEditor<Object> delegate = createEditor(content,
				new PropertyEditorContext<>(context, model, value, delegateProperty));
		delegate.getBody().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	private void rebuildUi() {
		UiUtils.disposeChildren(content);
		buildUi();
		content.layout(true, true);
		content.redraw();
	}

	@Override
	protected void updateValue(P value) {
		rebuildUi();
	}
}
