package com.gurella.engine.editor.ui;

import com.gurella.engine.editor.ui.viewer.EditorListViewer.LabelProvider;

public interface EditorTableColumn<ELEMENT> extends EditorItem {
	Alignment getAlignment();

	boolean getMoveable();

	EditorTable getParent();

	boolean getResizable();

	String getToolTipText();

	int getWidth();
	
	LabelProvider<ELEMENT> getLabelProvider();

	void pack();

	void setAlignment(Alignment alignment);

	void setMoveable(boolean moveable);

	void setResizable(boolean resizable);

	void setToolTipText(String string);

	void setWidth(int width);
	
	void setLabelProvider(LabelProvider<ELEMENT> labelProvider);
}
