package com.gurella.engine.editor.property;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorUiFactory;

public interface PropertyEditorFactory<P> {
	void buildUi(EditorComposite parent, EditorUiFactory uiFactory, PropertyEditorContext<P> context);
}
