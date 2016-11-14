package com.gurella.studio.editor.control;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.RIGHT;

public enum ViewOrientation {
	left(LEFT), right(RIGHT), bottom(BOTTOM);

	public final int swtValue;

	private ViewOrientation(int swtValue) {
		this.swtValue = swtValue;
	}
}