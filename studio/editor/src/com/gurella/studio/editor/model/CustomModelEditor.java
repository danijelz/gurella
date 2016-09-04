package com.gurella.studio.editor.model;

import org.eclipse.swt.widgets.Composite;

public class CustomModelEditor<T> extends MetaModelEditor<T> {
	public CustomModelEditor(Composite parent, ModelEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void initEditors() {
		// TODO Auto-generated method stub
	}
}
