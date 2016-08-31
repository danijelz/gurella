package com.gurella.engine.editor.ui;

public interface EditorTree extends EditorBaseComposite {
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

	void setSelection(EditorTreeItem item);

	void setSelection(EditorTreeItem[] items);

	void setSortColumn(EditorTreeColumn column);

	void setSortDirection(int direction);

	void setTopItem(EditorTreeItem item);

	void showColumn(EditorTreeColumn column);

	void showItem(EditorTreeItem item);

	void showSelection();

	EditorTreeColumn createColumn();

	EditorTreeColumn createColumn(int index);

	EditorTreeColumn createColumn(Alignment alignment);

	EditorTreeColumn createColumn(int index, Alignment alignment);

	EditorTreeItem createItem();

	EditorTreeItem createItem(int index);

	public static class TreeStyle extends ScrollableStyle<TreeStyle> {
		public boolean check;
		public boolean multiSelection;
		public boolean fullSelection;
		public boolean noScroll;
		public boolean virtual;

		public TreeStyle check(boolean check) {
			this.check = check;
			return cast();
		}

		public TreeStyle multiSelection(boolean multiSelection) {
			this.multiSelection = multiSelection;
			return cast();
		}

		public TreeStyle fullSelection(boolean fullSelection) {
			this.fullSelection = fullSelection;
			return cast();
		}

		public TreeStyle virtual(boolean virtual) {
			this.virtual = virtual;
			return cast();
		}

		public TreeStyle noScroll(boolean noScroll) {
			this.noScroll = noScroll;
			return cast();
		}
	}
}
