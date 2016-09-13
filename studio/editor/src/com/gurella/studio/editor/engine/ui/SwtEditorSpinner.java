package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.widgets.Spinner;

import com.gurella.engine.editor.ui.EditorSpinner;

public class SwtEditorSpinner extends SwtEditorBaseComposite<Spinner> implements EditorSpinner {
	public SwtEditorSpinner(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Spinner(parent.widget, style));
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
	public int getDigits() {
		return widget.getDigits();
	}

	@Override
	public int getIncrement() {
		return widget.getIncrement();
	}

	@Override
	public int getMaximum() {
		return widget.getMaximum();
	}

	@Override
	public int getMinimum() {
		return widget.getMinimum();
	}

	@Override
	public int getPageIncrement() {
		return widget.getPageIncrement();
	}

	@Override
	public int getSelection() {
		return widget.getSelection();
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public int getTextLimit() {
		return widget.getTextLimit();
	}

	@Override
	public void paste() {
		widget.paste();
	}

	@Override
	public void setDigits(int value) {
		widget.setDigits(value);
	}

	@Override
	public void setIncrement(int value) {
		widget.setIncrement(value);
	}

	@Override
	public void setMaximum(int value) {
		widget.setMaximum(value);
	}

	@Override
	public void setMinimum(int value) {
		widget.setMinimum(value);
	}

	@Override
	public void setPageIncrement(int value) {
		widget.setPageIncrement(value);
	}

	@Override
	public void setSelection(int value) {
		widget.setSelection(value);
	}

	@Override
	public void setTextLimit(int limit) {
		widget.setTextLimit(limit);
	}

	@Override
	public void setValues(int selection, int minimum, int maximum, int digits, int increment, int pageIncrement) {
		widget.setValues(selection, minimum, maximum, digits, increment, pageIncrement);
	}
}
