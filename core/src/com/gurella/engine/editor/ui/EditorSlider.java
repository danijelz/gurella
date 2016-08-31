package com.gurella.engine.editor.ui;

public interface EditorSlider extends EditorControl {
	int getIncrement();

	int getMaximum();

	int getMinimum();

	int getPageIncrement();

	int getSelection();

	int getThumb();

	void setIncrement(int value);

	void setMaximum(int value);

	void setMinimum(int value);

	void setPageIncrement(int value);

	void setSelection(int value);

	void setThumb(int value);

	void setValues(int selection, int minimum, int maximum, int thumb, int increment, int pageIncrement);

	public static class SliderStyle extends ControlStyle {
		public boolean vertical;
	}
}
