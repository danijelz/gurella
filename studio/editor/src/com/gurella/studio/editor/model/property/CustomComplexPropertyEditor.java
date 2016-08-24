package com.gurella.studio.editor.model.property;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.model.extension.SwtEditorUiFactory;

public class CustomComplexPropertyEditor<P> extends ComplexPropertyEditor<P> {
	public CustomComplexPropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		factory.buildUi(SwtEditorUiFactory.instance.createComposite(parent), new ContextAdapter<P>(context));
	}
}
