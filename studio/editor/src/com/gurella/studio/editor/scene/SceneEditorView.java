package com.gurella.studio.editor.scene;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.RIGHT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.GurellaSceneEditor;
import com.gurella.studio.editor.SceneEditorContext;

public abstract class SceneEditorView extends Composite {
	protected GurellaSceneEditor editor;

	public SceneEditorView(GurellaSceneEditor editor, String title, Image image, int style) {
		super(editor.getPartControl().getDockItemParent(style), checkStyle(style));
		this.editor = editor;
		editor.getPartControl().addItem(style, title, image, this);
	}

	private static int checkStyle(int style) {
		return ((style & LEFT) == 0 && (style & RIGHT) == 0 && (style & BOTTOM) == 0) ? style | LEFT : style;
	}

	public GurellaSceneEditor getSceneEditor() {
		return editor;
	}
	
	public SceneEditorContext getSceneEditorContext() {
		return editor.getEditorContext();
	}
}
