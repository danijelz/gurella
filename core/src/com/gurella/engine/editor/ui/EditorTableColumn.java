package com.gurella.engine.editor.ui;

public interface EditorTableColumn extends EditorItem {
	int getAlignment();

	boolean getMoveable();

	EditorTable getParent();

	boolean getResizable();

	String getToolTipText();

	int getWidth();

	void pack();

	void setAlignment(int alignment);

	void setMoveable(boolean moveable);

	void setResizable(boolean resizable);

	void setToolTipText(String string);

	void setWidth(int width);
}
