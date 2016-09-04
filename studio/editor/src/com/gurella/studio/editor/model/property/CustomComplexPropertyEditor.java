package com.gurella.studio.editor.model.property;

import static com.gurella.studio.editor.model.extension.SwtEditorUi.createComposite;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;

public class CustomComplexPropertyEditor<P> extends ComplexPropertyEditor<P> {
	public CustomComplexPropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		factory.buildUi(createComposite(body), new PropertyEditorContextAdapter<P>(context, this));
	}
}
