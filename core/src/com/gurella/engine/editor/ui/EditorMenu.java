package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.EditorMenuItem.MenuItemType;
import com.gurella.engine.editor.ui.style.WidgetStyle;

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

	EditorMenuItem createItem(MenuItemType type);

	EditorMenuItem createItem(int index, MenuItemType type);
	
	EditorMenu createSubMenu();
}
