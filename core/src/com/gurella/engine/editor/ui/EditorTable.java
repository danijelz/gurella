package com.gurella.engine.editor.ui;

public interface EditorTable extends EditorComposite {
	void clear(int index);

	void clear(int[] indices);

	void clear(int start, int end);

	void clearAll();

	void deselect(int index);

	void deselect(int[] indices);

	void deselect(int start, int end);

	void deselectAll();

	EditorTableColumn getColumn(int index);

	int getColumnCount();

	int[] getColumnOrder();

	EditorTableColumn[] getColumns();

	int getGridLineWidth();

	int getHeaderHeight();

	boolean getHeaderVisible();

	EditorTableItem getItem(int index);

	EditorTableItem getItem(int x, int y);

	int getItemCount();

	int getItemHeight();

	EditorTableItem[] getItems();

	boolean getLinesVisible();

	EditorTableItem[] getSelection();

	int getSelectionCount();

	int getSelectionIndex();

	int getSortDirection();

	int getTopIndex();

	int indexOf(EditorTableColumn column);

	int indexOf(EditorTableItem item);

	boolean isSelected(int index);

	void remove(int index);

	void remove(int[] indices);

	void remove(int start, int end);

	void removeAll();

	void select(int index);

	void select(int[] indices);

	void select(int start, int end);

	void selectAll();

	void setColumnOrder(int[] order);

	void setHeaderVisible(boolean show);

	void setItemCount(int count);

	void setLinesVisible(boolean show);

	void setSelection(int index);

	void setSelection(int[] indices);

	void setSelection(int start, int end);

	void setSelection(EditorTableItem item);

	void setSelection(EditorTableItem[] items);

	void setSortColumn(EditorTableColumn column);

	void setSortDirection(int direction);

	void setTopIndex(int index);

	void showColumn(EditorTableColumn column);

	void showItem(EditorTableItem item);

	void showSelection();
}
