package com.gurella.engine.editor;

public interface EditorUiFactory {
	EditorComposite createComposite(EditorComposite parent);
	
	EditorLabel createLabel(EditorComposite parent);

	EditorLabel createLabel(EditorComposite parent, String text);
}
