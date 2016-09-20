package com.gurella.engine.editor.ui.layout;

import com.gurella.engine.editor.ui.EditorControl;

public class EditorLayoutData {
	public static final int DEFAULT_HINT = -1;

	public VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
	public HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	public int widthHint = DEFAULT_HINT;
	public int heightHint = DEFAULT_HINT;
	public int horizontalIndent = 0;
	public int verticalIndent = 0;
	public int horizontalSpan = 1;
	public int verticalSpan = 1;
	public boolean grabExcessHorizontalSpace = false;
	public boolean grabExcessVerticalSpace = false;
	public int minimumWidth = 0;
	public int minimumHeight = 0;
	public boolean exclude = false;

	public EditorLayoutData verticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public EditorLayoutData horizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public EditorLayoutData alignment(HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		horizontalAlignment = hAlign;
		verticalAlignment = vAlign;
		return this;
	}

	public EditorLayoutData widthHint(int widthHint) {
		this.widthHint = widthHint;
		return this;
	}

	public EditorLayoutData heightHint(int heightHint) {
		this.heightHint = heightHint;
		return this;
	}

	public EditorLayoutData sizeHint(int wHint, int hHint) {
		widthHint = wHint;
		heightHint = hHint;
		return this;
	}

	public EditorLayoutData horizontalIndent(int horizontalIndent) {
		this.horizontalIndent = horizontalIndent;
		return this;
	}

	public EditorLayoutData verticalIndent(int verticalIndent) {
		this.verticalIndent = verticalIndent;
		return this;
	}

	public EditorLayoutData indent(int hIndent, int vIndent) {
		horizontalIndent = hIndent;
		verticalIndent = vIndent;
		return this;
	}

	public EditorLayoutData horizontalSpan(int horizontalSpan) {
		this.horizontalSpan = horizontalSpan;
		return this;
	}

	public EditorLayoutData verticalSpan(int verticalSpan) {
		this.verticalSpan = verticalSpan;
		return this;
	}

	public EditorLayoutData span(int hSpan, int vSpan) {
		horizontalSpan = hSpan;
		verticalSpan = vSpan;
		return this;
	}

	public EditorLayoutData grabExcessHorizontalSpace(boolean grabExcessHorizontalSpace) {
		this.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		return this;
	}

	public EditorLayoutData grabExcessVerticalSpace(boolean grabExcessVerticalSpace) {
		this.grabExcessVerticalSpace = grabExcessVerticalSpace;
		return this;
	}

	public EditorLayoutData grab(boolean horizontal, boolean vertical) {
		grabExcessHorizontalSpace = horizontal;
		grabExcessVerticalSpace = vertical;
		return this;
	}

	public EditorLayoutData minimumWidth(int minimumWidth) {
		this.minimumWidth = minimumWidth;
		return this;
	}

	public EditorLayoutData minimumHeight(int minimumHeight) {
		this.minimumHeight = minimumHeight;
		return this;
	}

	public EditorLayoutData minSize(int minW, int minH) {
		minimumWidth = minW;
		minimumHeight = minH;
		return this;
	}

	public EditorLayoutData exclude(boolean exclude) {
		this.exclude = exclude;
		return this;
	}

	public void applyTo(EditorControl control) {
		control.setLayoutData(this);
	}

	public enum HorizontalAlignment {
		LEFT, CENTER, RIGHT, FILL;
	}

	public enum VerticalAlignment {
		TOP, CENTER, BOTTOM, FILL;
	}
}
