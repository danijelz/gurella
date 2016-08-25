package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorCombo;

public class SwtEditorCombo extends SwtEditorBaseComposite<Combo> implements EditorCombo {
	public SwtEditorCombo(SwtEditorBaseComposite<?> parent) {
		super(parent);
	}

	@Override
	Combo createWidget(Composite parent) {
		return new Combo(parent, style);
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
	public GridPoint2 getSelection() {
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
	public void setSelection(GridPoint2 selection) {
		widget.setSelection(new Point(selection.x, selection.y));
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
}
