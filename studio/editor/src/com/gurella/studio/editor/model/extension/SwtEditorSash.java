package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorSash;

public class SwtEditorSash extends SwtEditorControl<Sash> implements EditorSash {
	public SwtEditorSash(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		super(parent);
	}

	@Override
	Sash createWidget(Composite parent) {
		return new Sash(parent, 0);
	}
}
