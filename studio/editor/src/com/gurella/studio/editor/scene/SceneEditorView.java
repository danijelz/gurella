package com.gurella.studio.editor.scene;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.scene.Scene;
import com.gurella.studio.editor.GurellaEditor;

public abstract class SceneEditorView extends Composite {
	private GurellaEditor editor;

	public SceneEditorView(GurellaEditor editor, SceneEditorMainContainer mainContainer, String title, Image image,
			int style) {
		super(mainContainer.getDockItemParent(style), style);
		mainContainer.addItem(style, title, image, this);
		this.editor = editor;
	}

	public abstract void present(Scene scene);

	protected void setDirty() {
		editor.setDirty();
	}
}
