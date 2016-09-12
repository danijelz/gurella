package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorExpandableComposite extends SwtEditorBaseExpandableComposite<ExpandableComposite> {
	SwtEditorExpandableComposite(SwtEditorComposite parent, int style) {
		super(parent, style);
	}

	@Override
	ExpandableComposite createWidget(Composite parent, int style) {
		return GurellaStudioPlugin.getToolkit().createExpandableComposite(parent, style);
	}
}
