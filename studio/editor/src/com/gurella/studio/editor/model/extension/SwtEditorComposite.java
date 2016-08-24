package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SwtEditorComposite extends SwtEditorBaseComposite<Composite> {
	public SwtEditorComposite(Composite composite) {
		super(composite);
	}

	public SwtEditorComposite(SwtEditorBaseComposite<?> parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	@Override
	Composite createWidget(Composite parent, FormToolkit toolkit) {
		return toolkit.createComposite(parent);
	}
}
