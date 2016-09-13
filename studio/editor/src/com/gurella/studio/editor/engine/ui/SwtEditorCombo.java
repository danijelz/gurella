package com.gurella.studio.editor.engine.ui;

import static com.gurella.engine.utils.Values.cast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorItem;
import com.gurella.engine.utils.Values;
import com.gurella.studio.editor.engine.ui.view.ListViewerLabelProvider;

public class SwtEditorCombo<ELEMENT> extends SwtEditorBaseComposite<Combo> implements EditorCombo<ELEMENT> {
	ComboViewer viewer;

	public SwtEditorCombo(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Combo(parent.widget, style));
		viewer = new ComboViewer(widget);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
	}

	@Override
	public void clearSelection() {
		widget.clearSelection();
	}

	@Override
	public void copy() {
		widget.copy();
	}

	@Override
	public void cut() {
		widget.cut();
	}

	@Override
	public void deselect(int index) {
		widget.deselect(index);
	}

	@Override
	public void deselectAll() {
		widget.deselectAll();
	}

	@Override
	public GridPoint2 getCaretLocation() {
		Point point = widget.getCaretLocation();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public int getCaretPosition() {
		return widget.getCaretPosition();
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
	public boolean getListVisible() {
		return widget.getListVisible();
	}

	@Override
	public GridPoint2 getSelectionPoint() {
		Point point = widget.getSelection();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public int getSelectionIndex() {
		return widget.getSelectionIndex();
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public int getTextHeight() {
		return widget.getTextHeight();
	}

	@Override
	public int getTextLimit() {
		return widget.getTextLimit();
	}

	@Override
	public int getVisibleItemCount() {
		return widget.getVisibleItemCount();
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
	public void paste() {
		widget.paste();
	}

	@Override
	public void remove(int index) {
		widget.remove(index);
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
	public void setListVisible(boolean visible) {
		widget.setListVisible(visible);
	}

	@Override
	public void setSelection(int x, int y) {
		widget.setSelection(new Point(x, y));
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	public void setTextLimit(int limit) {
		widget.setTextLimit(limit);
	}

	@Override
	public void setVisibleItemCount(int count) {
		widget.setVisibleItemCount(count);
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
	public void setSelection(List<ELEMENT> selection) {
		viewer.setSelection(new StructuredSelection(selection));
	}

	@Override
	public void setSelection(@SuppressWarnings("unchecked") ELEMENT... selection) {
		viewer.setSelection(new StructuredSelection(selection));
	}

	@Override
	public void setSelection(List<ELEMENT> selection, boolean reveal) {
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
