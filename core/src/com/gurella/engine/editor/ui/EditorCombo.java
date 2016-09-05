package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.viewer.EditorListViewer;

public interface EditorCombo<ELEMENT> extends EditorBaseComposite, EditorListViewer<ELEMENT> {
	void add(String string);

	void add(String string, int index);

	void clearSelection();

	void copy();

	void cut();

	void deselect(int index);

	void deselectAll();

	GridPoint2 getCaretLocation();

	int getCaretPosition();

	String getItem(int index);

	int getItemCount();

	int getItemHeight();

	String[] getItems();

	boolean getListVisible();

	GridPoint2 getSelectionPoint();

	int getSelectionIndex();

	String getText();

	int getTextHeight();

	int getTextLimit();

	int getVisibleItemCount();

	int indexOf(String string);

	int indexOf(String string, int start);

	void paste();

	void remove(int index);

	void remove(int start, int end);

	void remove(String string);

	void removeAll();

	void select(int index);

	void setItem(int index, String string);

	void setItems(String... items);

	void setListVisible(boolean visible);

	void setSelection(int x, int y);

	void setText(String string);

	void setTextLimit(int limit);

	void setVisibleItemCount(int count);

	public static class ComboStyle extends ScrollableStyle<ComboStyle> {
		public boolean readOnly;

		public ComboStyle readOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return this;
		}
	}
}
