package com.gurella.studio.editor.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

final class DockableTabFolder extends CTabFolder {
	DockableTabFolder(Dockable parent, int style) {
		super(parent, style);
	}

	@Override
	public Dockable getParent() {
		return (Dockable) super.getParent();
	}

	@Override
	public Rectangle getClientArea() {
		checkWidget();
		Rectangle trim = getParent().computeTabFolderTrim(CTabFolderRenderer.PART_BODY, SWT.FILL, 0, 0, 0, 0);
		Point size = getSize();

		if (!getParent().isContentVisible()) {
			return new Rectangle(-trim.x, -trim.y, 0, 0);
		} else {
			int width = size.x - trim.width;
			int height = size.y - trim.height;
			return new Rectangle(-trim.x, -trim.y, width, height);
		}
	}
}