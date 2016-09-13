package com.gurella.studio.editor.engine.model;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.model.DefaultMetaModelEditor;

public class CustomModelEditor<T> extends DefaultMetaModelEditor<T> {
	public CustomModelEditor(Composite parent, ModelEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void initEditors() {
		// TODO Auto-generated method stub
	}
}
