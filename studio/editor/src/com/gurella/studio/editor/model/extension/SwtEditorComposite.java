package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorComposite extends SwtEditorBaseComposite<Composite> {
	public SwtEditorComposite(Composite composite) {
		super(composite);
	}

	public SwtEditorComposite(SwtEditorBaseComposite<?> parent) {
		super(parent);
	}

	@Override
	Composite createWidget(Composite parent) {
		return GurellaStudioPlugin.getToolkit().createComposite(parent);
	}
}
