package com.gurella.engine.scene.renderable;

import com.gurella.engine.editor.property.PropertyEditorContext;
import com.gurella.engine.editor.property.PropertyEditorFactory;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorUiFactory;

public class InputEventsPropertyEditorFactory implements PropertyEditorFactory<Byte> {
	@Override
	public void buildUi(EditorComposite parent, PropertyEditorContext<Byte> context) {
		EditorUiFactory uiFactory = parent.getUiFactory();
		uiFactory.createLabel(parent, "test");
	}
}
