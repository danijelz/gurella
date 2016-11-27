package com.gurella.engine.editor.model;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.metatype.Model;
import com.gurella.engine.metatype.Property;

public interface ModelEditorContext<T> {
	Model<T> getModel();

	T getModelInstance();

	EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property);

	EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property, EditorLayoutData layoutData);

	EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property);

	EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property, EditorLayoutData layoutData);
	
	EditorComposite createModelEditor(EditorComposite parent, Object modelInstance);

	EditorComposite createModelEditor(EditorComposite parent, Object modelInstance, EditorLayoutData layoutData);

	void propertyValueChanged(Property<?> property, Object oldValue, Object newValue);

	EditorUi getEditorUi();
}
