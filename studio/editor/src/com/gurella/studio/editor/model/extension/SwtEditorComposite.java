package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorComposite extends SwtEditorLayoutComposite<Composite> implements EditorComposite {
	public SwtEditorComposite(Composite composite) {
		super(composite);
	}

	public SwtEditorComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Composite(parent.widget, style));
		GurellaStudioPlugin.getToolkit().adapt(widget);
	}
}
