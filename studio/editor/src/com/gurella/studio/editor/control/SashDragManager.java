package com.gurella.studio.editor.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

class SashDragManager extends MouseAdapter implements MouseMoveListener {
	private final Dockable dockable;

	protected boolean dragging = false;
	protected boolean correctState = false;
	protected boolean mouseDown = false;
	protected int originPosition;

	SashDragManager(Dockable dockable) {
		this.dockable = dockable;
	}

	@Override
	public void mouseDown(MouseEvent me) {
		if (me.button != 1) {
			return;
		}
		mouseDown = true;
		correctState = this.dockable.expanded || !this.dockable.tabFolder.getMinimized();
		originPosition = this.dockable.position == SWT.BOTTOM ? me.y : me.x;
	}

	@Override
	public void mouseMove(MouseEvent me) {
		if (mouseDown) {
			dragging = true;
		}

		if (dragging && correctState) {
			if (this.dockable.position == SWT.BOTTOM) {
				this.dockable.handleSashDragged(me.y - originPosition);
			} else {
				this.dockable.handleSashDragged(me.x - originPosition);
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent me) {
		dragging = false;
		correctState = false;
		mouseDown = false;
		this.dockable.layoutParent();
	}
}