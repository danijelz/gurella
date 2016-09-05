package com.gurella.engine.editor.ui.viewer;

import java.util.List;

public interface EditorListViewer<ELEMENT, SELECTION> extends EditorViewer<List<ELEMENT>, ELEMENT, Iterable<ELEMENT>> {
	void add(ELEMENT element);

	void add(ELEMENT... elements);

	void add(Iterable<ELEMENT> elements);

	ELEMENT getElementAt(int index);

	void insert(ELEMENT element, int position);

	void remove(ELEMENT element);

	void remove(ELEMENT... elements);

	void remove(Iterable<ELEMENT> elements);
}
