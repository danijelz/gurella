package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorCombo extends EditorBaseComposite {
	void add(String string);

	void add(String string, int index);

	void clearSelection();

	void copy();

	void cut();

	void deselect(int index);

	void deselectAll();

	GridPoint2 getCaretLocation();

	int getCaretPosition();

	String getItem(int index);

	int getItemCount();

	int getItemHeight();

	String[] getItems();

	boolean getListVisible();

	GridPoint2 getSelection();

	int getSelectionIndex();

	String getText();

	int getTextHeight();

	int getTextLimit();

	int getVisibleItemCount();

	int indexOf(String string);

	int indexOf(String string, int start);

	void paste();

	void remove(int index);

	void remove(int start, int end);

	void remove(String string);

	void removeAll();

	void select(int index);

	void setItem(int index, String string);

	void setItems(String... items);

	void setListVisible(boolean visible);

	void setSelection(GridPoint2 selection);

	void setText(String string);

	void setTextLimit(int limit);

	void setVisibleItemCount(int count);

	public static class ComboStyle extends ScrollableStyle {
		public boolean readOnly;
	}
}
