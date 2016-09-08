package com.gurella.engine.editor.ui.viewer;

import java.util.List;

import com.gurella.engine.editor.ui.EditorImage;

public interface EditorListViewer<ELEMENT> extends EditorViewer<List<ELEMENT>, ELEMENT, List<ELEMENT>> {
	void add(ELEMENT element);

	void add(@SuppressWarnings("unchecked") ELEMENT... elements);

	void add(Iterable<ELEMENT> elements);

	ELEMENT getElementAt(int index);

	void insert(ELEMENT element, int position);

	void remove(ELEMENT element);

	void remove(@SuppressWarnings("unchecked") ELEMENT... elements);

	void remove(Iterable<ELEMENT> elements);

	public interface LabelProvider<ELEMENT> {
		EditorImage getImage(ELEMENT element);

		String getText(ELEMENT element);
	}
}
