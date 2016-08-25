package com.gurella.engine.editor.ui;

public interface EditorToolBar extends EditorComposite {
	EditorToolItem getItem(int index);

	EditorToolItem getItem(int x, int y);

	int getItemCount();

	EditorToolItem[] getItems();

	int getRowCount();

	int indexOf(EditorToolItem item);
}
