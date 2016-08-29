package com.gurella.engine.editor.ui;

public interface EditorTree extends EditorComposite {
	void clear(int index, boolean all);

	void clearAll(boolean all);

	void deselect(EditorTreeItem item);

	void deselectAll();

	EditorTreeColumn getColumn(int index);

	int getColumnCount();

	int[] getColumnOrder();

	EditorTreeColumn[] getColumns();

	int getGridLineWidth();

	int getHeaderHeight();

	boolean getHeaderVisible();

	EditorTreeItem getItem(int index);

	EditorTreeItem getItem(int x, int y);

	int getItemCount();

	int getItemHeight();

	EditorTreeItem[] getItems();

	boolean getLinesVisible();

	EditorTreeItem getParentItem();

	EditorTreeItem[] getSelection();

	int getSelectionCount();

	EditorTreeColumn getSortColumn();

	int getSortDirection();

	EditorTreeItem getTopItem();

	int indexOf(EditorTreeColumn column);

	int indexOf(EditorTreeItem item);

	void removeAll();

	void select(EditorTreeItem item);

	void selectAll();

	void setColumnOrder(int[] order);

	void setHeaderVisible(boolean show);

	void setInsertMark(EditorTreeItem item, boolean before);

	void setItemCount(int count);

	void setLinesVisible(boolean show);

	void setRedraw(boolean redraw);

	void setSelection(EditorTreeItem item);

	void setSelection(EditorTreeItem[] items);

	void setSortColumn(EditorTreeColumn column);

	void setSortDirection(int direction);

	void setTopItem(EditorTreeItem item);

	void showColumn(EditorTreeColumn column);

	void showItem(EditorTreeItem item);

	void showSelection();
}
