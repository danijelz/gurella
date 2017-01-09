package com.gurella.studio.editor.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.gurella.studio.editor.SceneEditorContext;

public abstract class DockableView {
	public final int editorId;
	public final SceneEditorContext context;

	DockableViewComposite content;

	public DockableView(SceneEditorContext context) {
		editorId = context.editorId;
		this.context = context;
	}

	protected abstract String getTitle();

	protected abstract Image getImage();

	protected abstract void initControl(Composite control);

	void createControl(Composite parent) {
		content = new DockableViewComposite(parent);
		initControl(content);
	}

	public Composite getContent() {
		return content;
	}

	public Shell getShell() {
		return content.getShell();
	}

	public Display getDisplay() {
		return content.getDisplay();
	}

	public void layout() {
		content.layout(true, true);
	}

	class DockableViewComposite extends Composite {
		public DockableViewComposite(Composite parent) {
			super(parent, SWT.NONE);
		}

		DockableView getView() {
			return DockableView.this;
		}
	}
}
