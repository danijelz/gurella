package com.gurella.engine.editor.ui.layout;

public class EditorLayoutData {
	public int colspan = 1;
	public int rowspan = 1;
	public HorizontalAlign hAlign = HorizontalAlign.LEFT;
	public VerticalAlign vAlign = VerticalAlign.TOP;
	public int indent = 0;
	public int maxWidth = -1;
	public int maxHeight = -1;
	public int heightHint = -1;
	public boolean grabHorizontal;
	public boolean grabVertical;

	public EditorLayoutData colspan(int colspan) {
		this.colspan = colspan;
		return this;
	}

	public EditorLayoutData rowspan(int rowspan) {
		this.rowspan = rowspan;
		return this;
	}

	public EditorLayoutData hAlign(HorizontalAlign hAlign) {
		this.hAlign = hAlign;
		return this;
	}

	public EditorLayoutData vAlign(VerticalAlign vAlign) {
		this.vAlign = vAlign;
		return this;
	}

	public EditorLayoutData indent(int indent) {
		this.indent = indent;
		return this;
	}

	public EditorLayoutData maxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public EditorLayoutData maxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public EditorLayoutData heightHint(int heightHint) {
		this.heightHint = heightHint;
		return this;
	}

	public EditorLayoutData grabHorizontal(boolean grabHorizontal) {
		this.grabHorizontal = grabHorizontal;
		return this;
	}

	public EditorLayoutData grabVertical(boolean grabVertical) {
		this.grabVertical = grabVertical;
		return this;
	}

	public enum HorizontalAlign {
		LEFT, CENTER, RIGHT, FILL;
	}

	public enum VerticalAlign {
		TOP, MIDDLE, BOTTOM, FILL;
	}
}
