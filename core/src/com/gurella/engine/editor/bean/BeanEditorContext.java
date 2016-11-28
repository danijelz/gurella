package com.gurella.engine.editor.bean;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorLabel;
import com.gurella.engine.editor.ui.EditorUi;
import com.gurella.engine.editor.ui.layout.EditorLayoutData;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.Property;

public interface BeanEditorContext<T> {
	MetaType<T> getMetaType();

	T getBean();

	EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property);

	EditorComposite createPropertyEditor(EditorComposite parent, Property<?> property, EditorLayoutData layoutData);

	EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property);

	EditorLabel createPropertyLabel(EditorComposite parent, Property<?> property, EditorLayoutData layoutData);
	
	EditorComposite createBeanEditor(EditorComposite parent, Object bean);

	EditorComposite createBeanEditor(EditorComposite parent, Object bean, EditorLayoutData layoutData);

	void propertyValueChanged(Property<?> property, Object oldValue, Object newValue);

	EditorUi getEditorUi();
}
