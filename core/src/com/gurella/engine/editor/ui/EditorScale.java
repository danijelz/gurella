package com.gurella.engine.editor.ui;

public interface EditorScale extends EditorControl {
	int getIncrement();

	int getMaximum();

	int getMinimum();

	int getPageIncrement();

	int getSelection();

	void setIncrement(int increment);

	void setMaximum(int value);

	void setMinimum(int value);

	void setPageIncrement(int pageIncrement);

	void setSelection(int value);
}
