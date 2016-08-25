package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.gurella.engine.editor.ui.EditorGroup;

public class SwtEditorGroup extends SwtEditorBaseComposite<Group> implements EditorGroup {
	public SwtEditorGroup(SwtEditorBaseComposite<?> parent) {
		super(parent);
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}

	@Override
	Group createWidget(Composite parent) {
		return new Group(parent, style);
	}
}
