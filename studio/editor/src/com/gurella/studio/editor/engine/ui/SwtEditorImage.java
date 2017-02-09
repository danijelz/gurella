package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.gurella.engine.editor.ui.EditorImage;
import com.gurella.engine.math.GridRectangle;

public class SwtEditorImage implements EditorImage {
	final Image image;

	public SwtEditorImage(Image image) {
		if (image == null) {
			throw new NullPointerException("image is null");
		}
		this.image = image;
	}

	@Override
	public GridRectangle getBounds() {
		Rectangle bounds = image.getBounds();
		return new GridRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public void dispose() {
		image.dispose();
	}

	@Override
	public boolean isDisposed() {
		return image.isDisposed();
	}

	public Image getImage() {
		return image;
	}

	@Override
	public int hashCode() {
		return 31 + image.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		SwtEditorImage other = (SwtEditorImage) obj;
		return image.equals(other.image);
	}
}
