package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorTabFolder extends EditorComposite {
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

	EditorTabItem mewItem();

	EditorTabItem mewItem(int index);
}
