package com.gurella.studio.editor.model.property;

import org.eclipse.swt.widgets.Composite;

public abstract class SimplePropertyEditor<P> extends PropertyEditor<P> {
	public SimplePropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);
	}
}
