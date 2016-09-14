package com.gurella.studio.editor.engine.model;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.model.MetaModelEditor;

public class CustomModelEditor<T> extends MetaModelEditor<T> {
	public CustomModelEditor(Composite parent, CustomModelEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void createContent() {
		CustomModelEditorContextAdapter<T> context = getContext();
		context.factory.buildUi(SwtEditorUi.createComposite(this), context);
	}

	@Override
	public CustomModelEditorContextAdapter<T> getContext() {
		return (CustomModelEditorContextAdapter<T>) super.getContext();
	}
}
