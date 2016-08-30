package com.gurella.studio.editor.model.property;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.model.extension.SwtEditorUi;

public class CustomSimplePropertyEditor<P> extends SimplePropertyEditor<P> {
	public CustomSimplePropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		layout.marginHeight = 2;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		body.setLayout(layout);

		factory.buildUi(SwtEditorUi.createComposite(body), new ContextAdapter<P>(context));
	}
}
