package com.gurella.engine.editor.ui;

public interface EditorTabItem extends EditorItem {
	EditorControl getControl();

	EditorTabFolder getParent();

	String getToolTipText();

	void setControl(EditorControl control);

	void setImage(EditorImage image);

	void setToolTipText(String string);
}
