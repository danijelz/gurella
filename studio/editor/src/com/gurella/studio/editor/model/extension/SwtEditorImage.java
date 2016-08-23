package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.utils.GridRectangle;

public class SwtEditorImage implements EditorImage {
	private Image image;

	public SwtEditorImage(Image image) {
		this.image = image;
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = image.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}
}
