package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.EditorToolItem.ToolItemType;

public interface EditorToolBar extends EditorComposite {
	EditorToolItem getItem(int index);

	EditorToolItem getItem(int x, int y);

	int getItemCount();

	EditorToolItem[] getItems();

	int getRowCount();

	int indexOf(EditorToolItem item);

	EditorToolItem createItem(ToolItemType type);

	EditorToolItem createItem(int index, ToolItemType type);
}
