package com.gurella.engine.editor.ui;

import com.badlogic.gdx.math.GridPoint2;

public interface EditorText extends EditorScrollable {
	void append(String string);

	void clearSelection();

	void copy();

	void cut();

	int getCaretLineNumber();

	GridPoint2 getCaretLocation();

	int getCaretPosition();

	int getCharCount();

	boolean getDoubleClickEnabled();

	char getEchoChar();

	boolean getEditable();

	int getLineCount();

	String getLineDelimiter();

	int getLineHeight();

	String getMessage();

	GridPoint2 getSelection();

	int getSelectionCount();

	String getSelectionText();

	int getTabs();

	String getText();

	String getText(int start, int end);

	char[] getTextChars();

	int getTextLimit();

	int getTopIndex();

	int getTopPixel();

	void insert(String string);

	void paste();

	void selectAll();

	void setDoubleClickEnabled(boolean doubleClick);

	void setEchoChar(char echo);

	void setEditable(boolean editable);

	void setMessage(String message);

	void setSelection(int start);

	void setSelection(int start, int end);

	void setTabs(int tabs);

	void setText(String string);

	void setTextChars(char[] text);

	void setTextLimit(int limit);

	void setTopIndex(int index);

	void showSelection();

	public static class TextStyle extends ScrollableStyle<TextStyle> {
		public boolean wrap;
		public boolean readOnly;
		public boolean password;
		public Alignment alignment;
		public boolean formBorder;

		public TextStyle wWrap(boolean wrap) {
			this.wrap = wrap;
			return cast();
		}

		public TextStyle readOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return cast();
		}

		public TextStyle password(boolean password) {
			this.password = password;
			return cast();
		}

		public TextStyle alignment(Alignment alignment) {
			this.alignment = alignment;
			return cast();
		}

		public TextStyle formBorder(boolean formBorder) {
			this.formBorder = formBorder;
			return cast();
		}
	}
}
