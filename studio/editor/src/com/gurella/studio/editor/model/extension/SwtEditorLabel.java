package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.gurella.engine.editor.ui.Alignment;
import com.gurella.engine.editor.ui.EditorLabel;

public class SwtEditorLabel extends SwtEditorControl<Label> implements EditorLabel {
	public SwtEditorLabel(SwtEditorComposite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	public SwtEditorLabel(SwtEditorComposite parent, FormToolkit toolkit, String text) {
		super(parent, toolkit);
		setText(text);
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
	public Alignment getAlignment() {
		return SwtEditorUiFactoryUtils.alignmentFromSwt(widget.getAlignment());
	}

	@Override
	public void setAlignment(Alignment alignment) {
		widget.setAlignment(SwtEditorUiFactoryUtils.alignmentToSwt(alignment));
	}

	@Override
	Label createWidget(Composite parent, FormToolkit toolkit) {
		return toolkit.createLabel(parent, "");
	}
}
