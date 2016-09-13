package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.widgets.Scale;

import com.gurella.engine.editor.ui.EditorScale;

public class SwtEditorScale extends SwtEditorControl<Scale> implements EditorScale {
	public SwtEditorScale(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Scale(parent.widget, style));
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
	public void setIncrement(int increment) {
		widget.setIncrement(increment);
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
	public void setPageIncrement(int pageIncrement) {
		widget.setPageIncrement(pageIncrement);
	}

	@Override
	public void setSelection(int value) {
		widget.setSelection(value);
	}
}
