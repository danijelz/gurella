package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorComposite extends SwtEditorLayoutComposite<Composite> implements EditorComposite {
	public SwtEditorComposite(Composite composite) {
		super(composite);
	}

	public SwtEditorComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(GurellaStudioPlugin.getToolkit().createComposite(parent.widget, style));
	}
}
