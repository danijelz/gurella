package com.gurella.studio.editor.scene;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.gurella.engine.scene.Scene;

public abstract class SceneEditorView extends Composite {
	public SceneEditorView(SceneEditorMainContainer mainContainer, String title, Image image, int style) {
		super(mainContainer.getDockItemParent(style), style);
		mainContainer.addItem(style, title, image, this);
	}

	public abstract void present(Scene scene);
}
