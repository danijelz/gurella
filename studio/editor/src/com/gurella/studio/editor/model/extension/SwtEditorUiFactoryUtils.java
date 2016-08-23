package com.gurella.studio.editor.model.extension;

import org.eclipse.swt.SWT;

import com.gurella.engine.editor.ui.Alignment;

public class SwtEditorUiFactoryUtils {
	private SwtEditorUiFactoryUtils() {
	}

	public static Alignment alignmentFromSwt(int alignment) {
		switch (alignment) {
		case SWT.LEFT:
			return Alignment.LEFT;
		case SWT.CENTER:
			return Alignment.CENTER;
		case SWT.RIGHT:
			return Alignment.RIGHT;
		default:
			return null;
		}
	}

	public static int alignmentToSwt(Alignment alignment) {
		switch (alignment) {
		case LEFT:
			return SWT.LEFT;
		case CENTER:
			return SWT.CENTER;
		case RIGHT:
			return SWT.RIGHT;
		default:
			throw new IllegalArgumentException();
		}
	}
}
