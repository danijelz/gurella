package com.gurella.engine.editor.ui;

public interface EditorGroup extends EditorComposite {
	String getText();

	void setText(String string);

	public static class GroupStyle extends ScrollableStyle<GroupStyle> {
	}
}
