package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorTabFolder extends EditorBaseComposite {
	EditorTabItem getItem(int index);

	EditorTabItem getItem(GridPoint2 point);

	int getItemCount();

	EditorTabItem[] getItems();

	EditorTabItem[] getSelection();

	int getSelectionIndex();

	int indexOf(EditorTabItem item);

	void setSelection(int index);

	void setSelection(EditorTabItem item);

	void setSelection(EditorTabItem[] items);

	EditorTabItem createItem();

	EditorTabItem createItem(int index);

	public static class TabFolderStyle extends ScrollableStyle<TabFolderStyle> {
		public boolean bottom;

		public TabFolderStyle bottom(boolean bottom) {
			this.bottom = bottom;
			return this;
		}
	}
}
