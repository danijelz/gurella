package com.gurella.engine.editor.ui;

public interface EditorMenuItem extends EditorItem {
	int getID();

	void setID(int id);

	int getAccelerator();

	void setAccelerator(int accelerator);

	boolean isEnabled();

	boolean getEnabled();

	void setEnabled(boolean enabled);

	EditorMenu getParent();

	EditorMenu getMenu();

	void setMenu(EditorMenu menu);

	boolean getSelection();

	void setSelection(boolean selected);
}
