package com.gurella.engine.editor.ui.viewer;

import java.util.List;

public interface EditorColumnViewer<ELEMENT, SELECTION> extends EditorViewer<List<ELEMENT>, ELEMENT, SELECTION> {
	LabelProvider<ELEMENT> getLabelProvider(int columnIndex);

	void setLabelProvider(int columnIndex, LabelProvider<ELEMENT> labelProvider);
}
