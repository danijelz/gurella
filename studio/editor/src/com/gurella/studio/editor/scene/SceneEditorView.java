package com.gurella.studio.editor.scene;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.studio.editor.GurellaEditor;

public abstract class SceneEditorView extends Composite {
	protected GurellaEditor editor;

	public SceneEditorView(GurellaEditor editor, String title, Image image, int style) {
		super(editor.getMainContainer().getDockItemParent(style), style);
		editor.getMainContainer().addItem(style, title, image, this);
		this.editor = editor;
	}

	protected void setDirty() {
		editor.setDirty();
	}
	
	protected final void postMessage(Object message, Object... additionalData) {
		editor.postMessage(this, message, additionalData);
	}
	
	@SuppressWarnings("unused")
	public void handleMessage(SceneEditorView source, Object message, Object... additionalData) {
		
	}
}
