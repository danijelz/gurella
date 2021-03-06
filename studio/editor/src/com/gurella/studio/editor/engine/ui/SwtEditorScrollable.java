package com.gurella.studio.editor.engine.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import com.gurella.engine.editor.ui.EditorScrollable;
import com.gurella.engine.math.GridRectangle;

public abstract class SwtEditorScrollable<T extends Scrollable> extends SwtEditorControl<T>
		implements EditorScrollable {
	SwtEditorScrollable(T scrollable) {
		super(scrollable);
	}

	@Override
	public GridRectangle getClientArea() {
		Rectangle area = widget.getClientArea();
		return new GridRectangle(area.x, area.y, area.width, area.height);
	}

	@Override
	public boolean areScrollbarsOverlayed() {
		return widget.getScrollbarsMode() == SWT.SCROLLBAR_OVERLAY;
	}

	@Override
	public SwtEditorScrollBar getHorizontalBar() {
		ScrollBar horizontalBar = widget.getHorizontalBar();
		if (horizontalBar == null) {
			return null;
		}

		SwtEditorScrollBar bar = getEditorWidget(horizontalBar);
		return bar == null ? new SwtEditorScrollBar(horizontalBar) : bar;
	}

	@Override
	public SwtEditorScrollBar getVerticalBar() {
		ScrollBar verticalBar = widget.getVerticalBar();
		if (verticalBar == null) {
			return null;
		}

		SwtEditorScrollBar bar = getEditorWidget(verticalBar);
		return bar == null ? new SwtEditorScrollBar(verticalBar) : bar;
	}
}
