package com.gurella.engine.editor.ui;

public interface EditorSash extends EditorControl {
	public static class SashStyle extends ControlStyle<SashStyle> {
		public boolean vertical;
		public boolean smooth;

		public SashStyle vertical(boolean vertical) {
			this.vertical = vertical;
			return this;
		}

		public SashStyle smooth(boolean smooth) {
			this.smooth = smooth;
			return this;
		}
	}
}
