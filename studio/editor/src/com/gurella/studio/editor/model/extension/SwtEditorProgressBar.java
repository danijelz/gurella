package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.ProgressBar;

import com.gurella.engine.editor.ui.EditorProgressBar;

public class SwtEditorProgressBar extends SwtEditorControl<ProgressBar> implements EditorProgressBar {
	public SwtEditorProgressBar(SwtEditorLayoutComposite<?> parent, int style) {
		super(new ProgressBar(parent.widget, style));
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
	public int getSelection() {
		return widget.getSelection();
	}

	@Override
	public int getState() {
		return widget.getState();
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
	public void setSelection(int value) {
		widget.setSelection(value);
	}

	@Override
	public void setState(int state) {
		widget.setState(state);
	}
}
