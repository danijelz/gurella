package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.EditorControl;
import com.gurella.engine.editor.ui.EditorSashForm;

public class SwtEditorSashForm extends SwtEditorBaseComposite<SashForm> implements EditorSashForm {
	public SwtEditorSashForm(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	public SwtEditorControl<?> getMaximizedControl() {
		return getEditorWidget(widget.getMaximizedControl());
	}

	@Override
	public int getSashWidth() {
		return widget.getSashWidth();
	}

	@Override
	public int[] getWeights() {
		return widget.getWeights();
	}

	@Override
	public void setMaximizedControl(EditorControl control) {
		widget.setMaximizedControl(control == null ? null : ((SwtEditorControl<?>) control).widget);
	}

	@Override
	public void setSashWidth(int width) {
		widget.setSashWidth(width);
	}

	@Override
	public void setWeights(int[] weights) {
		widget.setWeights(weights);
	}

	@Override
	SashForm createWidget(Composite parent, int style) {
		return new SashForm(parent, style);
	}
}
