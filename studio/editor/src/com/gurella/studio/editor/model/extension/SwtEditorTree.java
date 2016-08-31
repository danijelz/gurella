package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTree;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTree extends SwtEditorBaseComposite<Tree> implements EditorTree {
	public SwtEditorTree(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public void deselectAll() {
		widget.deselectAll();
	}

	@Override
	public SwtEditorTreeColumn getColumn(int index) {
		return getEditorWidget(widget.getColumn(index));
	}

	@Override
	public int getColumnCount() {
		return widget.getColumnCount();
	}

	@Override
	public int[] getColumnOrder() {
		return widget.getColumnOrder();
	}

	@Override
	public SwtEditorTreeColumn[] getColumns() {
		return Arrays.stream(widget.getColumns()).map(c -> getEditorWidget(c)).toArray(i -> new SwtEditorTreeColumn[i]);
	}

	@Override
	public int getGridLineWidth() {
		return widget.getGridLineWidth();
	}

	@Override
	public int getHeaderHeight() {
		return widget.getHeaderHeight();
	}

	@Override
	public boolean getHeaderVisible() {
		return widget.getHeaderVisible();
	}

	@Override
	public SwtEditorTreeItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public SwtEditorTreeItem getItem(int x, int y) {
		return getEditorWidget(widget.getItem(new Point(x, y)));
	}

	@Override
	public int getItemCount() {
		return widget.getItemCount();
	}

	@Override
	public int getItemHeight() {
		return widget.getItemHeight();
	}

	@Override
	public SwtEditorTreeItem[] getItems() {
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTreeItem[i]);
	}

	@Override
	public boolean getLinesVisible() {
		return widget.getLinesVisible();
	}

	@Override
	public SwtEditorTreeItem[] getSelection() {
		return Arrays.stream(widget.getSelection()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTreeItem[i]);
	}

	@Override
	public int getSelectionCount() {
		return widget.getSelectionCount();
	}

	@Override
	public int getSortDirection() {
		return widget.getSortDirection();
	}

	@Override
	public int indexOf(EditorTreeColumn column) {
		return widget.indexOf(((SwtEditorTreeColumn) column).widget);
	}

	@Override
	public int indexOf(EditorTreeItem item) {
		return widget.indexOf(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public void removeAll() {
		widget.removeAll();
	}

	@Override
	public void selectAll() {
		widget.selectAll();
	}

	@Override
	public void setColumnOrder(int[] order) {
		widget.setColumnOrder(order);
	}

	@Override
	public void setHeaderVisible(boolean show) {
		widget.setHeaderVisible(show);
	}

	@Override
	public void setItemCount(int count) {
		widget.setItemCount(count);
	}

	@Override
	public void setLinesVisible(boolean show) {
		widget.setLinesVisible(show);
	}

	@Override
	public void setSelection(EditorTreeItem item) {
		widget.setSelection(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public void setSelection(EditorTreeItem[] items) {
		widget.setSelection(
				Arrays.stream(items).map(i -> ((SwtEditorTreeItem) i).widget).toArray(i -> new TreeItem[i]));
	}

	@Override
	public void setSortColumn(EditorTreeColumn column) {
		widget.setSortColumn(((SwtEditorTreeColumn) column).widget);
	}

	@Override
	public void setSortDirection(int direction) {
		widget.setSortDirection(direction);
	}

	@Override
	public void showColumn(EditorTreeColumn column) {
		widget.showColumn(((SwtEditorTreeColumn) column).widget);

	}

	@Override
	public void showItem(EditorTreeItem item) {
		widget.showItem(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public void showSelection() {
		widget.showSelection();
	}

	@Override
	public void clear(int index, boolean all) {
		widget.clear(index, all);
	}

	@Override
	public void clearAll(boolean all) {
		widget.clearAll(all);
	}

	@Override
	public void deselect(EditorTreeItem item) {
		widget.deselect(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public EditorTreeItem getParentItem() {
		return getEditorWidget(widget.getParentItem());
	}

	@Override
	public EditorTreeColumn getSortColumn() {
		return getEditorWidget(widget.getSortColumn());
	}

	@Override
	public EditorTreeItem getTopItem() {
		return getEditorWidget(widget.getTopItem());
	}

	@Override
	public void select(EditorTreeItem item) {
		widget.select(((SwtEditorTreeItem) item).widget);
	}

	@Override
	public void setInsertMark(EditorTreeItem item, boolean before) {
		widget.setInsertMark(((SwtEditorTreeItem) item).widget, before);
	}

	@Override
	public void setTopItem(EditorTreeItem item) {
		widget.setTopItem(((SwtEditorTreeItem) item).widget);
	}

	@Override
	Tree createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createTree(parent, style);
	}

	@Override
	public SwtEditorTreeColumn createColumn() {
		return new SwtEditorTreeColumn(this, 0);
	}

	@Override
	public SwtEditorTreeColumn createColumn(int index) {
		return new SwtEditorTreeColumn(this, index, 0);
	}

	@Override
	public SwtEditorTreeColumn createColumn(Alignment alignment) {
		return new SwtEditorTreeColumn(this, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTreeColumn createColumn(int index, Alignment alignment) {
		return new SwtEditorTreeColumn(this, index, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTreeItem createItem() {
		return new SwtEditorTreeItem(this);
	}

	@Override
	public SwtEditorTreeItem createItem(int index) {
		return new SwtEditorTreeItem(this, index);
	}
}
