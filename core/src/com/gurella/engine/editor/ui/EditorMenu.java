package com.gurella.engine.editor.ui;

public interface EditorMenu extends EditorWidget {
	EditorMenuItem getDefaultItem();

	void setDefaultItem(EditorMenuItem item);

	boolean getEnabled();

	EditorMenuItem getItem(int index);

	int getItemCount();

	EditorMenuItem[] getItems();

	int indexOf(EditorMenuItem item);

	Direction getOrientation();

	void setOrientation(Direction orientation);

	EditorComposite getParent();

	EditorMenuItem getParentItem();

	EditorMenu getParentMenu();

	boolean isVisible();

	boolean getVisible();

	void setVisible(boolean visible);

	boolean isEnabled();

	void setEnabled(boolean enabled);

	void setLocation(int x, int y);
}
