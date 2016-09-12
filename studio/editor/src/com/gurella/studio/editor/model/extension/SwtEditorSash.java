package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;

import com.gurella.engine.editor.ui.EditorSash;

public class SwtEditorSash extends SwtEditorControl<Sash> implements EditorSash {
	public SwtEditorSash(SwtEditorLayoutComposite<?> parent, int style) {
		super(parent, style);
	}

	@Override
	Sash createWidget(Composite parent, int style) {
		return new Sash(parent, style);
	}
}
