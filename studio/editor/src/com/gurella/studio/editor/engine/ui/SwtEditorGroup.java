package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.widgets.Group;

import com.gurella.engine.editor.ui.EditorGroup;

public class SwtEditorGroup extends SwtEditorLayoutComposite<Group> implements EditorGroup {
	public SwtEditorGroup(SwtEditorLayoutComposite<?> parent, int style) {
		super(new Group(parent.widget, style));
	}

	@Override
	public String getText() {
		return widget.getText();
	}

	@Override
	public void setText(String string) {
		widget.setText(string);
	}
}
