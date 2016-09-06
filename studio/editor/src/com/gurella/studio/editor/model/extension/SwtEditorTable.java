package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;
import static com.gurella.studio.editor.model.extension.style.SwtWidgetStyle.alignment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorItem;
import com.gurella.engine.editor.ui.EditorTable;
import com.gurella.engine.editor.ui.EditorTableColumn;
import com.gurella.engine.editor.ui.EditorTableItem;
import com.gurella.engine.editor.ui.viewer.EditorListViewer.LabelProvider;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorTable<ELEMENT> extends SwtEditorBaseComposite<Table> implements EditorTable<ELEMENT> {
	TableViewer viewer;

	public SwtEditorTable(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	Table createWidget(Composite parent, int style) {
		Table table = GurellaStudioPlugin.getToolkit().createTable(parent, style);
		viewer = new TableViewer(table);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		return table;
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
		return Arrays.stream(widget.getColumns()).sequential().map(c -> getEditorWidget(c))
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
		return Arrays.stream(widget.getItems()).sequential().map(i -> getEditorWidget(i))
				.toArray(i -> new SwtEditorTableItem[i]);
	}

	@Override
	public boolean getLinesVisible() {
		return widget.getLinesVisible();
	}

	@Override
	public SwtEditorTableItem[] getSelectedItems() {
		return Arrays.stream(widget.getSelection()).sequential().map(i -> getEditorWidget(i))
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
		widget.setSelection(Arrays.stream(items).sequential().map(i -> ((SwtEditorTableItem) i).widget)
				.toArray(i -> new TableItem[i]));
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
	public EditorTableColumn createColumn() {
		return new SwtEditorTableColumn(this, 0);
	}

	@Override
	public EditorTableColumn createColumn(int index) {
		return new SwtEditorTableColumn(this, index, 0);
	}

	@Override
	public SwtEditorTableColumn createColumn(Alignment alignment) {
		return new SwtEditorTableColumn(this, alignment(alignment));
	}

	@Override
	public SwtEditorTableColumn createColumn(int index, Alignment alignment) {
		return new SwtEditorTableColumn(this, index, alignment(alignment));
	}

	@Override
	public SwtEditorTableItem createItem() {
		return new SwtEditorTableItem(this);
	}

	@Override
	public SwtEditorTableItem createItem(int index) {
		return new SwtEditorTableItem(this, index);
	}

	@Override
	public ViewerCell<ELEMENT> getCell(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ELEMENT> getInput() {
		return Arrays.asList(Values.<ELEMENT[]> cast(viewer.getInput()));
	}

	@Override
	public List<ELEMENT> getSelection() {
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		return cast(selection.toList());
	}

	@Override
	public EditorItem scrollDown(int x, int y) {
		return getEditorWidget(viewer.scrollDown(x, y));
	}

	@Override
	public EditorItem scrollUp(int x, int y) {
		return getEditorWidget(viewer.scrollUp(x, y));
	}

	@Override
	public void setInput(java.util.List<ELEMENT> input) {
		viewer.setInput(input == null ? new Object[0] : input.toArray());
	}

	@Override
	public void setSelection(java.util.List<ELEMENT> selection) {
		viewer.setSelection(new StructuredSelection(selection));
	}

	@Override
	public void setSelection(@SuppressWarnings("unchecked") ELEMENT... selection) {
		viewer.setSelection(new StructuredSelection(selection));
	}

	@Override
	public void setSelection(java.util.List<ELEMENT> selection, boolean reveal) {
		viewer.setSelection(new StructuredSelection(selection), reveal);
	}

	@Override
	public void setSelection(ELEMENT[] selection, boolean reveal) {
		viewer.setSelection(new StructuredSelection(selection), reveal);
	}

	@Override
	public void refresh() {
		viewer.refresh();
	}

	@Override
	public void refresh(boolean updateLabels) {
		viewer.refresh(updateLabels);
	}

	@Override
	public void refresh(ELEMENT element) {
		viewer.refresh(element);
	}

	@Override
	public void refresh(ELEMENT element, boolean updateLabels) {
		viewer.refresh(element, updateLabels);
	}

	@Override
	public void reveal(ELEMENT element) {
		viewer.reveal(element);
	}

	@Override
	public void update(ELEMENT[] elements, String[] properties) {
		viewer.update(elements, properties);
	}

	@Override
	public void update(ELEMENT element, String... properties) {
		viewer.update(element, properties);
	}

	@Override
	public void add(ELEMENT element) {
		viewer.add(element);
	}

	@Override
	public void add(@SuppressWarnings("unchecked") ELEMENT... elements) {
		viewer.add(elements);
	}

	@Override
	public void add(Iterable<ELEMENT> elements) {
		viewer.add(StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	public ELEMENT getElementAt(int index) {
		return (ELEMENT) viewer.getElementAt(index);
	}

	@Override
	public void insert(ELEMENT element, int position) {
		viewer.insert(element, position);
	}

	@Override
	public void remove(ELEMENT element) {
		viewer.remove(element);
	}

	@Override
	public void remove(@SuppressWarnings("unchecked") ELEMENT... elements) {
		viewer.remove(elements);
	}

	@Override
	public void remove(Iterable<ELEMENT> elements) {
		viewer.remove(StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	public void replace(ELEMENT element, int index) {
		viewer.replace(element, index);
	}

	@Override
	public void refresh(boolean updateLabels, boolean reveal) {
		viewer.refresh(updateLabels, reveal);
	}

	@Override
	public void refresh(ELEMENT element, boolean updateLabels, boolean reveal) {
		viewer.refresh(element, updateLabels, reveal);
	}

	@Override
	public LabelProvider<ELEMENT> getLabelProvider(int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLabelProvider(int columnIndex, LabelProvider<ELEMENT> labelProvider) {
		// TODO Auto-generated method stub

	}
}
