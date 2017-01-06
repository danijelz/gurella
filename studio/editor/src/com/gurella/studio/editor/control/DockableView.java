package com.gurella.studio.editor.control;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.RIGHT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.SceneEditorContext;

public abstract class DockableView extends Composite {
	public final int editorId;
	public final SceneEditorContext context;

	public DockableView(Dock dock, SceneEditorContext context, ViewOrientation orientation) {
		this(dock, context, orientation == null ? LEFT : orientation.swtValue);
	}

	public DockableView(Dock dock, SceneEditorContext context, int orientation) {
		super(dock.getDockItemParent(orientation), checkStyle(orientation));
		dock.addDockItem(this, getTitle(), getImage(), orientation);
		editorId = context.editorId;
		this.context = context;
	}

	private static int checkStyle(int style) {
		return ((style & LEFT) == 0 && (style & RIGHT) == 0 && (style & BOTTOM) == 0) ? style | LEFT : style;
	}

	protected abstract String getTitle();

	protected abstract Image getImage();
}
