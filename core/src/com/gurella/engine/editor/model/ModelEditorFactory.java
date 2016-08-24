package com.gurella.engine.editor.model;

import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.ui.EditorComposite;

public interface ModelEditorFactory<M> {
	void buildUi(EditorComposite parent, PropertyEditorContext<M> context);
}
