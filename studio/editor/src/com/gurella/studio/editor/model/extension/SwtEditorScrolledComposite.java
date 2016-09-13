package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.custom.ScrolledComposite;

public class SwtEditorScrolledComposite extends SwtEditorBaseScrolledComposite<ScrolledComposite> {
	public SwtEditorScrolledComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(new ScrolledComposite(parent.widget, style));
	}
}
