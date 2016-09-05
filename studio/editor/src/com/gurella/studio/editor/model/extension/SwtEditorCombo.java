package com.gurella.studio.editor.model.extension;

import static com.gurella.engine.utils.Values.cast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorCombo;
import com.gurella.engine.editor.ui.EditorItem;
import com.gurella.engine.editor.ui.viewer.EditorViewer;
import com.gurella.engine.utils.Values;

public class SwtEditorCombo<ELEMENT> extends SwtEditorBaseComposite<Combo> implements EditorCombo<ELEMENT> {
	ComboViewer viewer;

	public SwtEditorCombo(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	Combo createWidget(Composite parent, int style) {
		Combo combo = new Combo(parent, style);
		viewer = new ComboViewer(combo);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		return combo;
	}

	@Override
	public void add(String string) {
		widget.add(string);
	}

	@Override
	public void add(String string, int index) {
		widget.add(string, index);
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
	public void setItem(int index, String string) {
		widget.setItem(index, string);
	}

	@Override
	public void setItems(String... items) {
		widget.setItems(items);
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
	public void add(ELEMENT... elements) {
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
	public void insert(ELEMENT element, int position) {
		viewer.insert(element, position);
	}

	@Override
	public void remove(ELEMENT element) {
		viewer.remove(element);
	}

	@Override
	public void remove(ELEMENT... elements) {
		viewer.remove(elements);
	}

	@Override
	public void remove(Iterable<ELEMENT> elements) {
		viewer.remove(StreamSupport.stream(elements.spliterator(), false).toArray());
	}

	@Override
	public List<ELEMENT> getInput() {
		return Arrays.stream(Values.<ELEMENT[]> cast(viewer.getInput())).collect(Collectors.toList());
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
	public void setSelection(List<ELEMENT> selection, boolean reveal) {
		viewer.setSelection(new StructuredSelection(selection), reveal);
	}

	@Override
	public EditorViewer.IContentProvider getContentProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditorViewer.IBaseLabelProvider<ELEMENT> getLabelProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContentProvider(EditorViewer.IContentProvider contentProvider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLabelProvider(EditorViewer.IBaseLabelProvider<ELEMENT> labelProvider) {
		// TODO Auto-generated method stub

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
