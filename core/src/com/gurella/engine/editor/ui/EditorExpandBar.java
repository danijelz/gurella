package com.gurella.engine.editor.ui;

public interface EditorExpandBar extends EditorBaseComposite {
	EditorExpandItem getItem(int index);

	int getItemCount();

	EditorExpandItem[] getItems();

	int getSpacing();

	int indexOf(EditorExpandItem item);

	void setSpacing(int spacing);

	EditorExpandItem createItem();

	EditorExpandItem createItem(int index);

	public static class ExpandBarStyle extends ScrollableStyle<ExpandBarStyle> {
	}
}
