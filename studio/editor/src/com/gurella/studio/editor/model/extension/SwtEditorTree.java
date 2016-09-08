package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorItem;
import com.gurella.engine.editor.ui.EditorTree;
import com.gurella.engine.editor.ui.EditorTreeColumn;
import com.gurella.engine.editor.ui.EditorTreeItem;
import com.gurella.engine.editor.ui.viewer.EditorListViewer.LabelProvider;
import com.gurella.engine.utils.Values;
import com.gurella.studio.GurellaStudioPlugin;
import com.gurella.studio.editor.model.extension.style.SwtWidgetStyle;

public class SwtEditorTree<ELEMENT> extends SwtEditorBaseComposite<Tree> implements EditorTree<ELEMENT> {
	TreeViewer viewer;

	public SwtEditorTree(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	Tree createWidget(Composite parent, int style) {
		Tree tree = GurellaStudioPlugin.getToolkit().createTree(parent, style);
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		TreeLabelProviderAdapter labelProviderAdapter = new TreeLabelProviderAdapter(null);
		tree.addDisposeListener(e -> labelProviderAdapter.dispose());
		viewer.setLabelProvider(labelProviderAdapter);
		return tree;
	}

	@Override
	public void deselectAll() {
		widget.deselectAll();
	}

	@Override
	public SwtEditorTreeColumn<ELEMENT> getColumn(int index) {
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
	public SwtEditorTreeColumn<ELEMENT>[] getColumns() {
		return cast(Arrays.stream(widget.getColumns()).sequential().map(c -> getEditorWidget(c))
				.toArray(i -> new SwtEditorTreeColumn[i]));
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
	public SwtEditorTreeItem<ELEMENT> getItem(int index) {
		return getEditorWidget(widget.getItem(index));
	}

	@Override
	public SwtEditorTreeItem<ELEMENT> getItem(int x, int y) {
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
	public SwtEditorTreeItem<ELEMENT>[] getItems() {
		return cast(Arrays.stream(widget.getItems()).sequential().map(i -> getEditorWidget(i))
				.toArray(i -> new SwtEditorTreeItem[i]));
	}

	@Override
	public boolean getLinesVisible() {
		return widget.getLinesVisible();
	}

	@Override
	public SwtEditorTreeItem<ELEMENT>[] getSelectedItems() {
		return cast(Arrays.stream(widget.getSelection()).sequential().map(i -> getEditorWidget(i))
				.toArray(i -> new SwtEditorTreeItem[i]));
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
	public int indexOf(EditorTreeColumn<ELEMENT> column) {
		return widget.indexOf(((SwtEditorTreeColumn<?>) column).widget);
	}

	@Override
	public int indexOf(EditorTreeItem item) {
		return widget.indexOf(((SwtEditorTreeItem<?>) item).widget);
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
		widget.setSelection(((SwtEditorTreeItem<?>) item).widget);
	}

	@Override
	public void setSelection(EditorTreeItem[] items) {
		widget.setSelection(Arrays.stream(items).sequential().map(i -> ((SwtEditorTreeItem<?>) i).widget)
				.toArray(i -> new TreeItem[i]));
	}

	@Override
	public void setSortColumn(EditorTreeColumn<ELEMENT> column) {
		widget.setSortColumn(((SwtEditorTreeColumn<?>) column).widget);
	}

	@Override
	public void setSortDirection(int direction) {
		widget.setSortDirection(direction);
	}

	@Override
	public void showColumn(EditorTreeColumn<ELEMENT> column) {
		widget.showColumn(((SwtEditorTreeColumn<?>) column).widget);

	}

	@Override
	public void showItem(EditorTreeItem item) {
		widget.showItem(((SwtEditorTreeItem<?>) item).widget);
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
		widget.deselect(((SwtEditorTreeItem<?>) item).widget);
	}

	@Override
	public EditorTreeItem getParentItem() {
		return getEditorWidget(widget.getParentItem());
	}

	@Override
	public EditorTreeColumn<ELEMENT> getSortColumn() {
		return getEditorWidget(widget.getSortColumn());
	}

	@Override
	public EditorTreeItem getTopItem() {
		return getEditorWidget(widget.getTopItem());
	}

	@Override
	public void select(EditorTreeItem item) {
		widget.select(((SwtEditorTreeItem<?>) item).widget);
	}

	@Override
	public void setInsertMark(EditorTreeItem item, boolean before) {
		widget.setInsertMark(((SwtEditorTreeItem<?>) item).widget, before);
	}

	@Override
	public void setTopItem(EditorTreeItem item) {
		widget.setTopItem(((SwtEditorTreeItem<?>) item).widget);
	}

	@Override
	public SwtEditorTreeColumn<ELEMENT> createColumn() {
		return new SwtEditorTreeColumn<ELEMENT>(this, 0);
	}

	@Override
	public SwtEditorTreeColumn<ELEMENT> createColumn(int index) {
		return new SwtEditorTreeColumn<ELEMENT>(this, index, 0);
	}

	@Override
	public SwtEditorTreeColumn<ELEMENT> createColumn(Alignment alignment) {
		return new SwtEditorTreeColumn<ELEMENT>(this, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTreeColumn<ELEMENT> createColumn(int index, Alignment alignment) {
		return new SwtEditorTreeColumn<ELEMENT>(this, index, SwtWidgetStyle.alignment(alignment));
	}

	@Override
	public SwtEditorTreeItem<ELEMENT> createItem() {
		return new SwtEditorTreeItem<>(this);
	}

	@Override
	public SwtEditorTreeItem<ELEMENT> createItem(int index) {
		return new SwtEditorTreeItem<>(this, index);
	}

	@Override
	public void add(ELEMENT parentElement, ELEMENT childElement) {
		viewer.add(parentElement, childElement);
	}

	@Override
	public void add(ELEMENT parentElement, @SuppressWarnings("unchecked") ELEMENT... childElements) {
		viewer.add(parentElement, childElements);
	}

	@Override
	public void add(ELEMENT parentElement, Iterable<ELEMENT> elements) {
		viewer.add(parentElement, StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	public void collapseAll() {
		viewer.collapseAll();
	}

	@Override
	public void collapseToLevel(ELEMENT element, int level) {
		viewer.collapseToLevel(element, level);
	}

	@Override
	public void expandAll() {
		viewer.expandAll();
	}

	@Override
	public void expandToLevel(int level) {
		viewer.expandToLevel(level);
	}

	@Override
	public void expandToLevel(ELEMENT element, int level) {
		viewer.expandToLevel(element, level);
	}

	@Override
	public int getAutoExpandLevel() {
		return viewer.getAutoExpandLevel();
	}

	@Override
	public ELEMENT[] getExpandedElements() {
		return cast(viewer.getExpandedElements());
	}

	@Override
	public boolean getExpandedState(ELEMENT element) {
		return viewer.getExpandedState(element);
	}

	@Override
	public ELEMENT[] getVisibleExpandedElements() {
		return cast(viewer.getVisibleExpandedElements());
	}

	@Override
	public void insert(ELEMENT parentElement, ELEMENT element, int position) {
		viewer.insert(parentElement, element, position);
	}

	@Override
	public boolean isExpandable(ELEMENT element) {
		return viewer.isExpandable(element);
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
	public void remove(ELEMENT parent, @SuppressWarnings("unchecked") ELEMENT... elements) {
		viewer.remove(parent, elements);
	}

	@Override
	public void remove(ELEMENT parent, Iterable<ELEMENT> elements) {
		viewer.remove(parent, StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	public void setAutoExpandLevel(int level) {
		viewer.setAutoExpandLevel(level);
	}

	@Override
	public void setExpandedElements(@SuppressWarnings("unchecked") ELEMENT... elements) {
		viewer.setExpandedElements(elements);
	}

	@Override
	public void setExpandedElements(Iterable<ELEMENT> elements) {
		viewer.setExpandedElements(StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	public void setExpandedState(ELEMENT element, boolean expanded) {
		viewer.setExpandedState(element, expanded);
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
	public void setInput(List<ELEMENT> input) {
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
}
