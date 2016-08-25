package com.gurella.engine.editor.ui;

public interface EditorSpinner extends EditorComposite {
	void copy();

	void cut();

	int getDigits();

	int getIncrement();

	int getMaximum();

	int getMinimum();

	int getPageIncrement();

	int getSelection();

	String getText();

	int getTextLimit();

	void paste();

	void setDigits(int value);

	void setIncrement(int value);

	void setMaximum(int value);

	void setMinimum(int value);

	void setPageIncrement(int value);

	void setSelection(int value);

	void setTextLimit(int limit);

	void setValues(int selection, int minimum, int maximum, int digits, int increment, int pageIncrement);
}