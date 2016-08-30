package com.gurella.engine.editor.ui;

public interface EditorExpandItem extends EditorItem {
	EditorControl getControl();

	boolean getExpanded();

	int getHeaderHeight();

	int getHeight();

	EditorExpandBar getParent();

	void setControl(EditorControl control);

	void setExpanded(boolean expanded);

	void setHeight(int height);
}
