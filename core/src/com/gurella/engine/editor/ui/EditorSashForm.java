package com.gurella.engine.editor.ui;

public interface EditorSashForm extends EditorBaseComposite {
	EditorControl getMaximizedControl();

	int getSashWidth();

	int[] getWeights();

	void setMaximizedControl(EditorControl control);

	void setSashWidth(int width);

	void setWeights(int[] weights);

	public static class SashFormStyle extends ScrollableStyle<SashFormStyle> {
		public boolean vertical;
		public boolean smooth;

		public SashFormStyle vertical(boolean vertical) {
			this.vertical = vertical;
			return this;
		}

		public SashFormStyle smooth(boolean smooth) {
			this.smooth = smooth;
			return this;
		}
	}
}
