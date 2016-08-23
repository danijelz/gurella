package com.gurella.studio.editor.model.extension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.EditorComposite;
import com.gurella.engine.editor.EditorControl;

public class SwtEditorComposite extends SwtEditorControl<Composite> implements EditorComposite {
	public SwtEditorComposite(Composite control) {
		this.control = control;
		control.addListener(SWT.Dispose, e -> instances.remove(control));
		instances.put(control, this);
	}

	public SwtEditorComposite(SwtEditorComposite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	@Override
	Composite createControl(Composite parent, FormToolkit toolkit) {
		return toolkit.createComposite(parent);
	}

	@Override
	public List<EditorControl> getChildren() {
		return Arrays.<Control> stream(control.getChildren()).map(c -> instances.get(c)).collect(Collectors.toList());
	}

	@Override
	public void layout() {
		control.layout();
	}
}
