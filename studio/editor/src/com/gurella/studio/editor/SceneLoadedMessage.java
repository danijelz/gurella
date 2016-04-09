package com.gurella.studio.editor;

import com.gurella.engine.scene.Scene;

public class SceneLoadedMessage {
	public final Scene scene;

	public SceneLoadedMessage(Scene scene) {
		this.scene = scene;
	}
}
