package com.gurella.studio.editor.engine.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;

public class ColumnLabelProviderAdapter<ELEMENT> extends ColumnLabelProvider {
	LabelProvider<ELEMENT> labelProvider;

	public ColumnLabelProviderAdapter(LabelProvider<ELEMENT> labelProvider) {
		this.labelProvider = labelProvider;
	}

	@Override
	public Image getImage(Object element) {
		@SuppressWarnings("unchecked")
		ELEMENT casted = (ELEMENT) element;
		return SwtEditorWidget.toSwtImage(labelProvider.getImage(casted));
	}

	@Override
	public String getText(Object element) {
		@SuppressWarnings("unchecked")
		ELEMENT casted = (ELEMENT) element;
		return labelProvider.getText(casted);
	}
}