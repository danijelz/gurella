package com.gurella.engine.editor.ui;

import java.util.List;

import com.gurella.engine.editor.ui.viewer.EditorTreeViewer;

public interface EditorTree<ELEMENT> extends EditorBaseComposite, EditorTreeViewer<ELEMENT> {
	void clear(int index, boolean all);

	void clearAll(boolean all);

	void deselect(EditorTreeItem item);

	void deselectAll();

	EditorTreeColumn<ELEMENT> getColumn(int index);

	int getColumnCount();

	int[] getColumnOrder();

	EditorTreeColumn<ELEMENT>[] getColumns();

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

	EditorTreeItem[] getSelectedItems();

	int getSelectionCount();

	EditorTreeColumn<ELEMENT> getSortColumn();

	int getSortDirection();

	EditorTreeItem getTopItem();

	int indexOf(EditorTreeColumn<ELEMENT> column);

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

	void setSortColumn(EditorTreeColumn<ELEMENT> column);

	void setSortDirection(int direction);

	void setTopItem(EditorTreeItem item);

	void showColumn(EditorTreeColumn<ELEMENT> column);

	void showItem(EditorTreeItem item);

	void showSelection();

	EditorTreeColumn<ELEMENT> createColumn();

	EditorTreeColumn<ELEMENT> createColumn(int index);

	EditorTreeColumn<ELEMENT> createColumn(Alignment alignment);

	EditorTreeColumn<ELEMENT> createColumn(int index, Alignment alignment);

	EditorTreeItem createItem();

	EditorTreeItem createItem(int index);

	public static abstract class TreeContentProvider<ELEMENT> {
		public abstract <E extends ELEMENT> List<E> getChildren(ELEMENT item, int depth);

		public <E extends ELEMENT> TreeContentProvider<ELEMENT> getChildContentProvider(
				@SuppressWarnings("unused") E child, @SuppressWarnings("unused") int depth) {
			return this;
		}
	}

	public static class TreeStyle<ELEMENT> extends ScrollableStyle<TreeStyle<ELEMENT>> {
		public TreeContentProvider<ELEMENT> contentProvider;
		public boolean check;
		public boolean multiSelection;
		public boolean fullSelection;
		public boolean noScroll;
		public boolean virtual;
		public boolean formBorder;

		public TreeStyle(TreeContentProvider<ELEMENT> contentProvider) {
			this.contentProvider = contentProvider;
		}

		public TreeStyle<ELEMENT> check(boolean check) {
			this.check = check;
			return cast();
		}

		public TreeStyle<ELEMENT> multiSelection(boolean multiSelection) {
			this.multiSelection = multiSelection;
			return cast();
		}

		public TreeStyle<ELEMENT> fullSelection(boolean fullSelection) {
			this.fullSelection = fullSelection;
			return cast();
		}

		public TreeStyle<ELEMENT> virtual(boolean virtual) {
			this.virtual = virtual;
			return cast();
		}

		public TreeStyle<ELEMENT> noScroll(boolean noScroll) {
			this.noScroll = noScroll;
			return cast();
		}
		
		public TreeStyle<ELEMENT> formBorder(boolean formBorder) {
			this.formBorder = formBorder;
			return cast();
		}
	}
}
