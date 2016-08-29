package com.gurella.engine.editor.ui;

public interface EditorTreeColumn extends EditorItem {
	Alignment getAlignment();

	boolean getMoveable();

	EditorTree getParent();

	boolean getResizable();

	String getToolTipText();

	int getWidth();

	void pack();

	void setAlignment(Alignment alignment);

	void setMoveable(boolean moveable);

	void setResizable(boolean resizable);

	void setToolTipText(String string);

	void setWidth(int width);
}
