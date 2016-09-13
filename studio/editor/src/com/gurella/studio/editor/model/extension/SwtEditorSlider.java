package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Slider;

import com.gurella.engine.editor.ui.EditorSlider;

public class SwtEditorSlider extends SwtEditorControl<Slider> implements EditorSlider {
	public SwtEditorSlider(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Slider(parent.widget, style));
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

	@Override
	public int getThumb() {
		return widget.getThumb();
	}

	@Override
	public void setThumb(int value) {
		widget.setThumb(value);
	}

	@Override
	public void setValues(int selection, int minimum, int maximum, int thumb, int increment, int pageIncrement) {
		widget.setValues(selection, minimum, maximum, thumb, increment, pageIncrement);
	}
}
