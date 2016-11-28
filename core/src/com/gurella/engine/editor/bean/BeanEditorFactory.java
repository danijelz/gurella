package com.gurella.engine.editor.bean;

import com.gurella.engine.editor.ui.EditorComposite;

public interface BeanEditorFactory<M> {
	void buildUi(EditorComposite parent, BeanEditorContext<M> context);
}
