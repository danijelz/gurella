package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.custom.ScrolledComposite;

public class SwtEditorScrolledComposite extends SwtEditorBaseScrolledComposite<ScrolledComposite> {
	public SwtEditorScrolledComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(new ScrolledComposite(parent.widget, style));
	}
}
