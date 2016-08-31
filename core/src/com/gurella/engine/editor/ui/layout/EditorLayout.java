package com.gurella.engine.editor.ui.layout;

public class EditorLayout {
	public int numColumns = 1;
	public int leftMargin = 5;
	public int rightMargin = 5;
	public int topMargin = 5;
	public int bottomMargin = 5;
	public int horizontalSpacing = 5;
	public int verticalSpacing = 5;
	public boolean makeColumnsEqualWidth = false;

	public EditorLayout numColumns(int numColumns) {
		this.numColumns = numColumns;
		return this;
	}

	public EditorLayout leftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
		return this;
	}

	public EditorLayout rightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
		return this;
	}

	public EditorLayout topMargin(int topMargin) {
		this.topMargin = topMargin;
		return this;
	}

	public EditorLayout bottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
		return this;
	}

	public EditorLayout horizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
		return this;
	}

	public EditorLayout verticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
		return this;
	}

	public EditorLayout makeColumnsEqualWidth(boolean makeColumnsEqualWidth) {
		this.makeColumnsEqualWidth = makeColumnsEqualWidth;
		return this;
	}
}
