package com.gurella.engine.editor.ui.viewer;

import com.gurella.engine.editor.ui.EditorTreeItem;

public interface EditorTreeViewer<ELEMENT> extends EditorColumnViewer<ELEMENT, Iterable<EditorTreeItem>> {
	void add(ELEMENT parentElement, ELEMENT childElement);

	void add(ELEMENT parentElement, @SuppressWarnings("unchecked") ELEMENT... childElements);

	void add(ELEMENT parentElement, Iterable<ELEMENT> elements);

	void collapseAll();

	void collapseToLevel(ELEMENT element, int level);

	void expandAll();

	void expandToLevel(int level);

	void expandToLevel(ELEMENT element, int level);

	int getAutoExpandLevel();

	ELEMENT[] getExpandedElements();

	boolean getExpandedState(ELEMENT element);

	ELEMENT[] getVisibleExpandedElements();

	void insert(ELEMENT parentElement, ELEMENT element, int position);

	boolean isExpandable(ELEMENT element);

	void remove(ELEMENT elements);

	void remove(@SuppressWarnings("unchecked") ELEMENT... elements);

	void remove(Iterable<ELEMENT> elements);

	void remove(ELEMENT parent, @SuppressWarnings("unchecked") ELEMENT... elements);

	void remove(ELEMENT parent, Iterable<ELEMENT> elements);

	void setAutoExpandLevel(int level);

	void setExpandedElements(@SuppressWarnings("unchecked") ELEMENT... elements);

	void setExpandedElements(Iterable<ELEMENT> elements);

	void setExpandedState(ELEMENT element, boolean expanded);
}
