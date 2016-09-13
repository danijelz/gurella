package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

public class SwtEditorScrolledComposite extends SwtEditorBaseScrolledComposite<ScrolledComposite> {
	public SwtEditorScrolledComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	ScrolledComposite createWidget(Composite parent, int style) {
		return new ScrolledComposite(parent, style);
	}
}
