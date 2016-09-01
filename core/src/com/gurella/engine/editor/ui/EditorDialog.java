package com.gurella.engine.editor.ui;

public interface EditorDialog {
	EditorShell getParent();

	String getText();

	void setText(String string);
}
