package com.gurella.engine.editor.model;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;

public interface ModelEditorContext<T> {
	Model<T> model();

	T getModelInstance();

	EditorComposite createPropertyEditor(Property<?> property);

	EditorLabel createPropertyLabel(Property<?> property);
}
