package com.gurella.engine.editor.ui.viewer;

public interface EditorTableViewer<ELEMENT, SELECTION> extends EditorColumnViewer<ELEMENT, Iterable<ELEMENT>> {
	void add(ELEMENT element);

	void add(ELEMENT... elements);

	void add(Iterable<ELEMENT> elements);

	void clear(int index);

	ELEMENT getElementAt(int index);

	void insert(ELEMENT element, int position);

	void remove(ELEMENT element);

	void remove(ELEMENT... elements);

	void remove(Iterable<ELEMENT> elements);

	void replace(ELEMENT element, int index);

	void refresh(boolean updateLabels, boolean reveal);

	void refresh(ELEMENT element, boolean updateLabels, boolean reveal);
}
