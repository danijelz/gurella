package com.gurella.engine.editor.ui;

import java.io.InputStream;

public interface EditorUiFactory {
	EditorImage createImage(InputStream imageStream);

	EditorComposite createComposite(EditorComposite parent);

	EditorLabel createLabel(EditorComposite parent);

	EditorLabel createLabel(EditorComposite parent, String text);
}
