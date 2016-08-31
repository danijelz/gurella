package com.gurella.studio.editor.model.extension;

import java.util.Arrays;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorTable;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTable extends SwtEditorBaseComposite<Table> implements EditorTable {
	public SwtEditorTable(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public void clear(int index) {
		widget.clear(index);
	}

	@Override
	public void clear(int[] indices) {
		widget.clear(indices);
	}

	@Override
	public void clear(int start, int end) {
		widget.clear(start, end);
	}

	@Override
	public void clearAll() {
		widget.clearAll();
	}

	@Override
	public void deselect(int index) {
		widget.deselect(index);
	}

	@Override
	public void deselect(int[] indices) {
		widget.deselect(indices);
	}

	@Override
	public void deselect(int start, int end) {
		widget.deselect(start, end);
	}

	@Override
	public void deselectAll() {
		widget.deselectAll();
	}

	@Override
	public SwtEditorTableColumn getColumn(int index) {
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
	public SwtEditorTableColumn[] getColumns() {
		return Arrays.stream(widget.getColumns()).map(c -> getEditorWidget(c))
				.toArray(i -> new SwtEditorTableColumn[i]);
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
	public SwtEditorTableItem getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public SwtEditorTableItem getItem(int x, int y) {
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
	public SwtEditorTableItem[] getItems() {
		return Arrays.stream(widget.getItems()).map(i -> getEditorWidget(i)).toArray(i -> new SwtEditorTableItem[i]);
	}

	@Override
	public boolean getLinesVisible() {
		return widget.getLinesVisible();
	}

	@Override
	public SwtEditorTableItem[] getSelection() {
		return Arrays.stream(widget.getSelection()).map(i -> getEditorWidget(i))
				.toArray(i -> new SwtEditorTableItem[i]);
	}

	@Override
	public int getSelectionCount() {
		return widget.getSelectionCount();
	}

	@Override
	public int getSelectionIndex() {
		return widget.getSelectionIndex();
	}

	@Override
	public int getSortDirection() {
		return widget.getSortDirection();
	}

	@Override
	public int getTopIndex() {
		return widget.getTopIndex();
	}

	@Override
	public int indexOf(EditorTableColumn column) {
		return widget.indexOf(((SwtEditorTableColumn) column).widget);
	}

	@Override
	public int indexOf(EditorTableItem item) {
		return widget.indexOf(((SwtEditorTableItem) item).widget);
	}

	@Override
	public boolean isSelected(int index) {
		return widget.isSelected(index);
	}

	@Override
	public void remove(int index) {
		widget.remove(index);
	}

	@Override
	public void remove(int[] indices) {
		widget.remove(indices);
	}

	@Override
	public void remove(int start, int end) {
		widget.remove(start, end);
	}

	@Override
	public void removeAll() {
		widget.removeAll();
	}

	@Override
	public void select(int index) {
		widget.select(index);
	}

	@Override
	public void select(int[] indices) {
		widget.select(indices);
	}

	@Override
	public void select(int start, int end) {
		widget.select(start, end);
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
	public void setSelection(int index) {
		widget.setSelection(index);
	}

	@Override
	public void setSelection(int[] indices) {
		widget.setSelection(indices);
	}

	@Override
	public void setSelection(int start, int end) {
		widget.setSelection(start, end);
	}

	@Override
	public void setSelection(EditorTableItem item) {
		widget.setSelection(((SwtEditorTableItem) item).widget);
	}

	@Override
	public void setSelection(EditorTableItem[] items) {
		widget.setSelection(
				Arrays.stream(items).map(i -> ((SwtEditorTableItem) i).widget).toArray(i -> new TableItem[i]));
	}

	@Override
	public void setSortColumn(EditorTableColumn column) {
		widget.setSortColumn(((SwtEditorTableColumn) column).widget);
	}

	@Override
	public void setSortDirection(int direction) {
		widget.setSortDirection(direction);
	}

	@Override
	public void setTopIndex(int index) {
		widget.setTopIndex(index);
	}

	@Override
	public void showColumn(EditorTableColumn column) {
		widget.showColumn(((SwtEditorTableColumn) column).widget);

	}

	@Override
	public void showItem(EditorTableItem item) {
		widget.showItem(((SwtEditorTableItem) item).widget);
	}

	@Override
	public void showSelection() {
		widget.showSelection();
	}

	@Override
	Table createWidget(Composite parent, int style) {
		return new Table(parent, style);
	}

	@Override
	public EditorTableColumn createColumn() {
		return new SwtEditorTableColumn(this, 0);
	}

	@Override
	public EditorTableColumn createColumn(int index) {
		return new SwtEditorTableColumn(this, index, 0);
	}

	@Override
	public SwtEditorTableColumn createColumn(Alignment alignment) {
		return new SwtEditorTableColumn(this, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTableColumn createColumn(int index, Alignment alignment) {
		return new SwtEditorTableColumn(this, index, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTableItem createItem() {
		return new SwtEditorTableItem(this);
	}

	@Override
	public SwtEditorTableItem createItem(int index) {
		return new SwtEditorTableItem(this, index);
	}
}
