package com.gurella.studio.editor.model.extension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.EditorComposite;
import com.gurella.engine.editor.ui.EditorControl;

public class SwtEditorComposite extends SwtEditorControl<Composite> implements EditorComposite {
	public SwtEditorComposite(Composite composite) {
		init(composite);
	}

	public SwtEditorComposite(SwtEditorComposite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	@Override
	Composite createWidget(Composite parent, FormToolkit toolkit) {
		return toolkit.createComposite(parent);
	}

	@Override
	public List<EditorControl> getChildren() {
		return Arrays.<Control> stream(widget.getChildren()).map(c -> (EditorControl) instances.get(c))
				.collect(Collectors.toList());
	}

	@Override
	public void layout() {
		widget.layout();
	}
}
