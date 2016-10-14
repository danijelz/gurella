package com.gurella.studio.editor.control;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

class DockableTabFolderRenderer extends CTabFolderRenderer {
	protected DockableTabFolderRenderer(CTabFolder parent) {
		super(parent);
	}

	@Override
	protected Point computeSize(int part, int state, GC gc, int wHint, int hHint) {
		return super.computeSize(part, state, gc, wHint, hHint);
	}

	@Override
	protected Rectangle computeTrim(int part, int state, int x, int y, int width, int height) {
		return super.computeTrim(part, state, x, y, width, height);
	}
}