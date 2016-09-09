package com.gurella.studio.editor.model.extension;

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
		SwtEditorImage image = (SwtEditorImage) labelProvider.getImage(casted);
		return image == null ? null : image.image;
	}

	@Override
	public String getText(Object element) {
		@SuppressWarnings("unchecked")
		ELEMENT casted = (ELEMENT) element;
		return labelProvider.getText(casted);
	}
}