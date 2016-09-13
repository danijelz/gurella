package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import com.badlogic.gdx.math.GridPoint2;
import com.gurella.engine.editor.ui.EditorText;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorText extends SwtEditorScrollable<Text> implements EditorText {
	SwtEditorText(Text text) {
		super(text);
	}

	public SwtEditorText(SwtEditorLayoutComposite<?> parent, String text, int style) {
		super(GurellaStudioPlugin.getToolkit().createText(parent.widget, text, style));
	}

	@Override
	public void append(String string) {
		widget.append(string);
	}

	@Override
	public void clearSelection() {
		widget.clearSelection();
	}

	@Override
	public void copy() {
		widget.copy();
	}

	@Override
	public void cut() {
		widget.cut();
	}

	@Override
	public int getCaretLineNumber() {
		return widget.getCaretLineNumber();
	}

	@Override
	public GridPoint2 getCaretLocation() {
		Point point = widget.getCaretLocation();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public int getCaretPosition() {
		return widget.getCaretPosition();
	}

	@Override
	public int getCharCount() {
		return widget.getCharCount();
	}

	@Override
	public boolean getDoubleClickEnabled() {
		return widget.getDoubleClickEnabled();
	}

	@Override
	public char getEchoChar() {
		return widget.getEchoChar();
	}

	@Override
	public boolean getEditable() {
		return widget.getEditable();
	}

	@Override
	public int getLineCount() {
		return widget.getLineCount();
	}

	@Override
	public String getLineDelimiter() {
		return widget.getLineDelimiter();
	}

	@Override
	public int getLineHeight() {
		return widget.getLineHeight();
	}

	@Override
	public String getMessage() {
		return widget.getMessage();
	}

	@Override
	public GridPoint2 getSelection() {
		Point point = widget.getSelection();
		return new GridPoint2(point.x, point.y);
	}

	@Override
	public int getSelectionCount() {
		return widget.getSelectionCount();
	}

	@Override
	public String getSelectionText() {
		return widget.getSelectionText();
	}

	@Override
	public int getTabs() {
		return widget.getTabs();
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public String getText(int start, int end) {
		return widget.getText(start, end);
	}

	@Override
	public char[] getTextChars() {
		return widget.getTextChars();
	}

	@Override
	public int getTextLimit() {
		return widget.getTextLimit();
	}

	@Override
	public int getTopIndex() {
		return widget.getTopIndex();
	}

	@Override
	public int getTopPixel() {
		return widget.getTopPixel();
	}

	@Override
	public void insert(String string) {
		widget.insert(string);
	}

	@Override
	public void paste() {
		widget.paste();
	}

	@Override
	public void selectAll() {
		widget.selectAll();
	}

	@Override
	public void setDoubleClickEnabled(boolean doubleClick) {
		widget.setDoubleClickEnabled(doubleClick);
	}

	@Override
	public void setEchoChar(char echo) {
		widget.setEchoChar(echo);
	}

	@Override
	public void setEditable(boolean editable) {
		widget.setEditable(editable);
	}

	@Override
	public void setMessage(String message) {
		widget.setMessage(message);
	}

	@Override
	public void setSelection(int start) {
		widget.setSelection(start);
	}

	@Override
	public void setSelection(int start, int end) {
		widget.setSelection(start, end);
	}

	@Override
	public void setTabs(int tabs) {
		widget.setTabs(tabs);
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	public void setTextChars(char[] text) {
		widget.setTextChars(text);
	}

	@Override
	public void setTextLimit(int limit) {
		widget.setTextLimit(limit);
	}

	@Override
	public void setTopIndex(int index) {
		widget.setTopIndex(index);
	}

	@Override
	public void showSelection() {
		widget.showSelection();
	}
}
