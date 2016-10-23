package com.gurella.studio.editor.control;

import org.eclipse.swt.widgets.Control;

import com.gurella.studio.editor.utils.UiUtils;

class CollapseRunnable implements Runnable {
	private final Dockable dockable;

	CollapseRunnable(Dockable dockable) {
		this.dockable = dockable;
	}

	@Override
	public void run() {
		if (!this.dockable.expanded) {
			return;
		} else if (this.dockable.sashDragManager.dragging
				|| isDescendantOf(this.dockable.getDisplay().getCursorControl())) {
			UiUtils.getDisplay().timerExec(500, this);
		} else {
			this.dockable.expanded = false;
			this.dockable.stateChanged();
		}
	}

	private boolean isDescendantOf(Control descendant) {
		Control temp = descendant;
		if (temp == null) {
			return false;
		}

		while (temp != null) {
			if (this.dockable == temp) {
				return true;
			}
			temp = temp.getParent();
		}

		return false;
	}
}