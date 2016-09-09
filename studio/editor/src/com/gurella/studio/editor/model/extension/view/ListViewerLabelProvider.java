package com.gurella.studio.editor.model.extension.view;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.gurella.engine.editor.ui.viewer.EditorViewer.LabelProvider;
import com.gurella.studio.editor.model.extension.SwtEditorImage;

public class ListViewerLabelProvider<ELEMENT> extends BaseLabelProvider implements ILabelProvider {
	private LabelProvider<ELEMENT> labelProvider;

	public ListViewerLabelProvider(LabelProvider<ELEMENT> labelProvider) {
		this.labelProvider = labelProvider;
	}

	public LabelProvider<ELEMENT> getLabelProvider() {
		return labelProvider;
	}

	@Override
	public Image getImage(Object element) {
		@SuppressWarnings("unchecked")
		ELEMENT casted = (ELEMENT) element;
		SwtEditorImage image = (SwtEditorImage) labelProvider.getImage(casted);
		return image == null ? null : image.getImage();
	}

	@Override
	public String getText(Object element) {
		@SuppressWarnings("unchecked")
		ELEMENT casted = (ELEMENT) element;
		return labelProvider.getText(casted);
	}
}
