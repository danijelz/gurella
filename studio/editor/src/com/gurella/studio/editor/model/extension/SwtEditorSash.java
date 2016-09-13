package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Sash;

import com.gurella.engine.editor.ui.EditorSash;

public class SwtEditorSash extends SwtEditorControl<Sash> implements EditorSash {
	public SwtEditorSash(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Sash(parent.widget, style));
	}
}
