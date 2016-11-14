package com.gurella.studio.editor.control;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.RIGHT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.SceneEditor;
import com.gurella.studio.editor.SceneEditorContext;

public abstract class DockableView extends Composite {
	public final SceneEditorContext editorContext;

	public DockableView(SceneEditor editor, String title, Image image, ViewOrientation orientation) {
		this(editor, title, image, orientation == null ? LEFT : orientation.swtValue);
	}

	public DockableView(SceneEditor editor, String title, Image image, int style) {
		super(editor.getDock().getDockItemParent(style), checkStyle(style));
		editorContext = editor.getSceneContext();
		editor.getDock().addDockItem(style, title, image, this);
	}

	private static int checkStyle(int style) {
		return ((style & LEFT) == 0 && (style & RIGHT) == 0 && (style & BOTTOM) == 0) ? style | LEFT : style;
	}

	public enum ViewOrientation {
		left(LEFT), right(RIGHT), bottom(BOTTOM);

		public final int swtValue;

		private ViewOrientation(int swtValue) {
			this.swtValue = swtValue;
		}
	}
}
