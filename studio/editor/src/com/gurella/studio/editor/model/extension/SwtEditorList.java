package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorList;

public class SwtEditorList extends SwtEditorScrollable<List> implements EditorList {
	public SwtEditorList(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		super(parent, toolkit);
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
	public String[] getSelection() {
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
	public void setItem(int index, String string) {
		widget.setItem(index, string);
	}

	@Override
	public void setItems(String... items) {
		widget.setItems(items);
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
	List createWidget(Composite parent) {
		return new List(parent, 0);
	}
}
