package com.gurella.studio.editor.scene;

import static org.eclipse.swt.SWT.BOTTOM;
import static org.eclipse.swt.SWT.LEFT;
import static org.eclipse.swt.SWT.RIGHT;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.EditorMessageListener;
import com.gurella.studio.editor.GurellaSceneEditor;

public abstract class SceneEditorView extends Composite implements EditorMessageListener {
	protected GurellaSceneEditor editor;

	public SceneEditorView(GurellaSceneEditor editor, String title, Image image, int style) {
		super(editor.getPartControl().getDockItemParent(style), checkStyle(style));
		this.editor = editor;
		addDisposeListener(e -> this.editor.removeEditorMessageListener(this));
		editor.addEditorMessageListener(this);
		editor.getPartControl().addItem(style, title, image, this);
	}

	private static int checkStyle(int style) {
		return ((style & LEFT) == 0 && (style & RIGHT) == 0 && (style & BOTTOM) == 0) ? style | LEFT : style;
	}

	public final void postMessage(Object message) {
		editor.postMessage(this, message);
	}

	@Override
	public void handleMessage(Object source, Object message) {
	}

	public void addEditorMessageListener(EditorMessageListener listener) {
		editor.addEditorMessageListener(listener);
	}

	public void removeEditorMessageListener(EditorMessageListener listener) {
		editor.removeEditorMessageListener(listener);
	}
	
	public GurellaSceneEditor getSceneEditor() {
		return editor;
	}
}
