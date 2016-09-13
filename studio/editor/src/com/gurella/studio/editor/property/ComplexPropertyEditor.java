package com.gurella.studio.editor.property;

import org.eclipse.swt.widgets.Composite;

public abstract class ComplexPropertyEditor<P> extends PropertyEditor<P> {
	public ComplexPropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);
	}
}
