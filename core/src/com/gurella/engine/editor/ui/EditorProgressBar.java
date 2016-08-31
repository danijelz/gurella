package com.gurella.engine.editor.ui;

public interface EditorProgressBar extends EditorControl {
	int getMaximum();

	int getMinimum();

	int getSelection();

	int getState();

	void setMaximum(int value);

	void setMinimum(int value);

	void setSelection(int value);

	void setState(int state);

	public static class ProgressBarStyle extends ControlStyle<ProgressBarStyle> {
		public boolean vertical;
		public boolean smooth;
		public boolean indeterminate;

		public ProgressBarStyle vertical(boolean vertical) {
			this.vertical = vertical;
			return this;
		}

		public ProgressBarStyle smooth(boolean smooth) {
			this.smooth = smooth;
			return this;
		}

		public ProgressBarStyle indeterminate(boolean indeterminate) {
			this.indeterminate = indeterminate;
			return this;
		}
	}
}
