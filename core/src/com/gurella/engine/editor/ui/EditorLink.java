package com.gurella.engine.editor.ui;

public interface EditorLink extends EditorControl {
	String getText();

	void setText(String string);

	public static class LinkStyle extends ControlStyle {
	}
}
