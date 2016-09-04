package com.gurella.studio.editor.model.extension;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.gurella.engine.editor.ui.EditorLayoutComposite;
import com.gurella.engine.editor.ui.layout.EditorLayout;

public abstract class SwtEditorLayoutComposite<T extends Composite> extends SwtEditorBaseComposite<T>
		implements EditorLayoutComposite {
	public SwtEditorLayoutComposite() {
	}

	public SwtEditorLayoutComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	public EditorLayout getLayout() {
		Layout layout = widget.getLayout();
		return layout instanceof GridLayout ? SwtEditorUi.transformLayout((GridLayout) layout) : null;
	}

	@Override
	public EditorLayout getOrCreateLayout() {
		Layout layout = widget.getLayout();
		return layout instanceof GridLayout ? SwtEditorUi.transformLayout((GridLayout) layout) : new EditorLayout();
	}

	@Override
	public EditorLayout getOrCreateDefaultLayout() {
		Layout layout = widget.getLayout();
		GridLayout gridLayout = layout instanceof GridLayout ? (GridLayout) layout
				: GridLayoutFactory.fillDefaults().create();
		return SwtEditorUi.transformLayout(gridLayout);
	}

	@Override
	public void setLayout(EditorLayout layout) {
		widget.setLayout(SwtEditorUi.transformLayout(layout));
	}

	@Override
	public void setLayout(int numColumns) {
		Layout layout = widget.getLayout();
		if (layout instanceof GridLayout) {
			GridLayoutFactory.createFrom((GridLayout) layout).numColumns(numColumns).applyTo(widget);
		} else {
			GridLayoutFactory.fillDefaults().numColumns(numColumns).applyTo(widget);
		}
	}
}
