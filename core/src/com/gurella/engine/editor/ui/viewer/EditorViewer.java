package com.gurella.engine.editor.ui.viewer;

import com.gurella.engine.editor.ui.EditorItem;

public interface EditorViewer<INPUT, ELEMENT, SELECTION> {
	INPUT getInput();

	SELECTION getSelection();

	EditorItem scrollDown(int x, int y);

	EditorItem scrollUp(int x, int y);

	void setInput(INPUT input);

	void setSelection(SELECTION selection);

	void setSelection(SELECTION selection, boolean reveal);

	/////////////////////////// ContentViewer

	IContentProvider getContentProvider();

	IBaseLabelProvider<ELEMENT> getLabelProvider();

	void setContentProvider(IContentProvider contentProvider);

	void setLabelProvider(IBaseLabelProvider<ELEMENT> labelProvider);

	////////////////////////// StructuredViewer

	void refresh();

	void refresh(boolean updateLabels);

	void refresh(ELEMENT element);

	void refresh(ELEMENT element, boolean updateLabels);

	void reveal(ELEMENT element);

	void update(ELEMENT[] elements, String[] properties);

	void update(ELEMENT element, String... properties);

	interface IContentProvider {

	}

	interface IBaseLabelProvider<ELEMENT> {

	}
}
