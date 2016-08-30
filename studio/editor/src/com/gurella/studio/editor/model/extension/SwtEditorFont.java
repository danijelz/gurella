package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.gurella.engine.editor.ui.EditorFont;

public class SwtEditorFont implements EditorFont {
	final Font font;
	final FontData fontData;

	public SwtEditorFont(Font font) {
		this.font = font;
		fontData = font.getFontData()[0];
	}

	@Override
	public void dispose() {
		font.dispose();
	}

	@Override
	public boolean isDisposed() {
		return font.isDisposed();
	}

	@Override
	public String getName() {
		return fontData.getName();
	}

	@Override
	public int getHeight() {
		return fontData.getHeight();
	}

	@Override
	public boolean isBold() {
		return (fontData.getStyle() & SWT.BOLD) != 0;
	}

	@Override
	public boolean isItalic() {
		return (fontData.getStyle() & SWT.ITALIC) != 0;
	}

	@Override
	public int hashCode() {
		return 31 + font.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		SwtEditorFont other = (SwtEditorFont) obj;
		return font.equals(other.font);
	}
}
