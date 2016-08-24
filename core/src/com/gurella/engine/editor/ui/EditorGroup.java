package com.gurella.engine.editor.ui;

import com.gurella.engine.utils.GridRectangle;

public interface EditorGroup extends EditorComposite {
	GridRectangle getClientArea();

	String getText();

	void setText(String string);
}
