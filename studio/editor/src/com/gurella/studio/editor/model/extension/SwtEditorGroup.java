package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.gurella.engine.editor.ui.EditorGroup;
import com.gurella.engine.editor.ui.layout.EditorLayout;

public class SwtEditorGroup extends SwtEditorBaseComposite<Group> implements EditorGroup {
	public SwtEditorGroup(SwtEditorComposite parent, int style) {
		super(parent, style);
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
	Group createWidget(Composite parent, int style) {
		return new Group(parent, style);
	}

	@Override
	public EditorLayout getLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLayout(EditorLayout layout) {
		// TODO Auto-generated method stub

	}
}
