package com.gurella.studio.editor.scene;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.EditorMessageListener;
import com.gurella.studio.editor.GurellaEditor;

public abstract class SceneEditorView extends Composite implements EditorMessageListener {
	protected GurellaEditor editor;

	public SceneEditorView(GurellaEditor editor, String title, Image image, int style) {
		super(editor.getMainContainer().getDockItemParent(style), style);
		editor.getMainContainer().addItem(style, title, image, this);
		this.editor = editor;
		addDisposeListener(e -> this.editor.removeEditorMessageListener(this));
		editor.addEditorMessageListener(this);
	}

	protected void setDirty() {
		editor.setDirty();
	}

	protected final void postMessage(Object message) {
		editor.postMessage(this, message);
	}

	@Override
	public void handleMessage(Object source, Object message) {
	}
}
