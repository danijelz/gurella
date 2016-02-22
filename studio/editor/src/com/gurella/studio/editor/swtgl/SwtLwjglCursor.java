package com.gurella.studio.editor.swtgl;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class SwtLwjglCursor implements Cursor {
	org.eclipse.swt.graphics.Cursor swtCursor = null;

	public SwtLwjglCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		if (pixmap == null) {
			return;
		}

		if (pixmap.getFormat() != Pixmap.Format.RGBA8888) {
			throw new GdxRuntimeException("Cursor image pixmap is not in RGBA8888 format.");
		}

		if ((pixmap.getWidth() & (pixmap.getWidth() - 1)) != 0) {
			throw new GdxRuntimeException(
					"Cursor image pixmap width of " + pixmap.getWidth() + " is not a power-of-two greater than zero.");
		}

		if ((pixmap.getHeight() & (pixmap.getHeight() - 1)) != 0) {
			throw new GdxRuntimeException("Cursor image pixmap height of " + pixmap.getHeight()
					+ " is not a power-of-two greater than zero.");
		}

		if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
			throw new GdxRuntimeException("xHotspot coordinate of " + xHotspot
					+ " is not within image width bounds: [0, " + pixmap.getWidth() + ").");
		}

		if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
			throw new GdxRuntimeException("yHotspot coordinate of " + yHotspot
					+ " is not within image height bounds: [0, " + pixmap.getHeight() + ").");
		}

		PaletteData palette = new PaletteData(0xFF00, 0xFF0000, 0xFF000000);
		ImageData imageData = new ImageData(pixmap.getWidth(), pixmap.getHeight(), 32, palette);
		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				int rgba = pixmap.getPixel(x, y);
				imageData.setPixel(x, y, rgba);
				imageData.setAlpha(x, y, rgba & 0x000000ff);
			}
		}

		swtCursor = new org.eclipse.swt.graphics.Cursor(SwtLwjglGraphics.getDisplay(), imageData, xHotspot,
				pixmap.getHeight() - yHotspot - 4);
	}

	@Override
	public void dispose() {
		if (swtCursor != null) {
			swtCursor.dispose();
		}
	}
}
