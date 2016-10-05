package com.gurella.studio.editor.engine.property;

import static com.gurella.studio.editor.engine.ui.SwtEditorUi.createComposite;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.studio.editor.property.CompositePropertyEditor;
import com.gurella.studio.editor.property.PropertyEditorContext;

public class CustomCompositePropertyEditor<P> extends CompositePropertyEditor<P> {
	public CustomCompositePropertyEditor(Composite parent, PropertyEditorContext<?, P> context,
			PropertyEditorFactory<P> factory) {
		super(parent, context);
		factory.buildUi(createComposite(body), new CustomPropertyEditorContextAdapter<P>(context, this));
	}
}
