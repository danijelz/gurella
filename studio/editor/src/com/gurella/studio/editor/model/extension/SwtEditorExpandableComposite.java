package com.gurella.studio.editor.model.extension;

import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.gurella.studio.GurellaStudioPlugin;

public class SwtEditorExpandableComposite extends SwtEditorBaseExpandableComposite<ExpandableComposite> {
	SwtEditorExpandableComposite(SwtEditorLayoutComposite<?> parent, int style) {
		super(GurellaStudioPlugin.getToolkit().createExpandableComposite(parent.widget, style));
	}
}
