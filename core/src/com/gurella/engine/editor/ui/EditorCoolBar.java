package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorCoolBar extends EditorComposite {
	EditorCoolItem getItem(int index);

	int getItemCount();

	int[] getItemOrder();

	EditorCoolItem[] getItems();

	GridPoint2[] getItemSizes();

	boolean getLocked();

	int[] getWrapIndices();

	int indexOf(EditorCoolItem item);

	void setItemLayout(int[] itemOrder, int[] wrapIndices, GridPoint2[] sizes);

	void setLocked(boolean locked);

	void setWrapIndices(int[] indices);
	
	EditorCoolItem createItem(boolean dropDown);
}
