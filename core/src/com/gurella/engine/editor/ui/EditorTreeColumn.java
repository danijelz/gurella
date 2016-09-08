package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.viewer.EditorListViewer.LabelProvider;

public interface EditorTreeColumn<ELEMENT> extends EditorItem {
	Alignment getAlignment();

	boolean getMoveable();

	EditorTree<ELEMENT> getParent();

	boolean getResizable();

	String getToolTipText();
	
	LabelProvider<ELEMENT> getLabelProvider();

	int getWidth();

	void pack();

	void setAlignment(Alignment alignment);

	void setMoveable(boolean moveable);

	void setResizable(boolean resizable);

	void setToolTipText(String string);

	void setWidth(int width);
	
	void setLabelProvider(LabelProvider<ELEMENT> labelProvider);
}
