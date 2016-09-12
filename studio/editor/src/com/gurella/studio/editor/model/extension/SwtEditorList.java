package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import com.gurella.engine.editor.ui.EditorItem;
import com.gurella.engine.editor.ui.EditorList;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.model.extension.view.ListViewerLabelProvider;

public class SwtEditorList<ELEMENT> extends SwtEditorScrollable<List> implements EditorList<ELEMENT> {
	ListViewer viewer;

	public SwtEditorList(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	List createWidget(Composite parent, int style) {
		List list = new List(parent, style);
		viewer = new ListViewer(list);
		return list;
	}

	@Override
	public void deselect(int index) {
		widget.deselect(index);
	}

	@Override
	public void deselect(int[] indices) {
		widget.select(indices);
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
	public int getFocusIndex() {
		return widget.getFocusIndex();
	}

	@Override
	public String getItem(int index) {
		return widget.getItem(index);
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
	public String[] getItems() {
		return widget.getItems();
	}

	@Override
	public String[] getSelectedItems() {
		return widget.getSelection();
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
	public int[] getSelectionIndices() {
		return widget.getSelectionIndices();
	}

	@Override
	public int getTopIndex() {
		return widget.getTopIndex();
	}

	@Override
	public int indexOf(String string) {
		return widget.indexOf(string);
	}

	@Override
	public int indexOf(String string, int start) {
		return widget.indexOf(string, start);
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
	public void remove(String string) {
		widget.remove(string);
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
	public void setSelection(String[] items) {
		widget.setSelection(items);
	}

	@Override
	public void setTopIndex(int index) {
		widget.setTopIndex(index);
	}

	@Override
	public void showSelection() {
		widget.showSelection();
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
	public ELEMENT getElementAt(int index) {
		return cast(viewer.getElementAt(index));
	}

	@Override
	public void insert(int position, ELEMENT element) {
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
	public java.util.List<ELEMENT> getInput() {
		return Arrays.asList(Values.<ELEMENT[]> cast(viewer.getInput()));
	}

	@Override
	public java.util.List<ELEMENT> getSelection() {
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
	public LabelProvider<ELEMENT> getLabelProvider() {
		IBaseLabelProvider labelProvider = viewer.getLabelProvider();
		if (labelProvider instanceof ListViewerLabelProvider) {
			@SuppressWarnings("unchecked")
			ListViewerLabelProvider<ELEMENT> casted = (ListViewerLabelProvider<ELEMENT>) labelProvider;
			return casted.getLabelProvider();
		} else {
			return null;
		}
	}

	@Override
	public void setLabelProvider(LabelProvider<ELEMENT> labelProvider) {
		IBaseLabelProvider provider = labelProvider == null ? new org.eclipse.jface.viewers.LabelProvider()
				: new ListViewerLabelProvider<ELEMENT>(labelProvider);
		widget.addDisposeListener(e -> provider.dispose());
		viewer.setLabelProvider(provider);
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
