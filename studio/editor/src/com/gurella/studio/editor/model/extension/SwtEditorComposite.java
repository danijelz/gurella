package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorComposite extends SwtEditorLayoutComposite<Composite> implements EditorComposite {
	public SwtEditorComposite(Composite composite) {
		init(composite);
	}

	public SwtEditorComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	Composite createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createComposite(parent);
	}
}
