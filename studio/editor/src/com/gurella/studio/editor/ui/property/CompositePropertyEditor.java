package com.gurella.studio.editor.ui.property;

import org.eclipse.swt.widgets.Composite;

public abstract class CompositePropertyEditor<P> extends PropertyEditor<P> {
	public CompositePropertyEditor(Composite parent, PropertyEditorContext<?, P> context) {
		super(parent, context);
	}
}
