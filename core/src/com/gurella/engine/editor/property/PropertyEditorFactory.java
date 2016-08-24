package com.gurella.engine.editor.property;

import com.gurella.engine.editor.ui.EditorComposite;

public interface PropertyEditorFactory<P> {
	void buildUi(EditorComposite parent, PropertyEditorContext<P> context);
}
