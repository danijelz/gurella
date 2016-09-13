package com.gurella.studio.editor.engine.model;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.engine.ui.SwtEditorUi;
import com.gurella.studio.editor.model.MetaModelEditor;

public class CustomModelEditor<T> extends MetaModelEditor<T> {
	public CustomModelEditor(Composite parent, ModelEditorContextAdapter<T> context) {
		super(parent, context);
	}

	@Override
	protected void initEditors() {
		ModelEditorContextAdapter<T> context = getContext();
		context.factory.buildUi(SwtEditorUi.createComposite(this), context);
	}

	@Override
	public ModelEditorContextAdapter<T> getContext() {
		return (ModelEditorContextAdapter<T>) super.getContext();
	}
}
