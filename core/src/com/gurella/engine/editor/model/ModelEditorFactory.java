package com.gurella.engine.editor.model;

import com.gurella.engine.editor.ui.EditorComposite;

public interface ModelEditorFactory<M> {
	void buildUi(EditorComposite parent, ModelEditorContext<M> context);
}
