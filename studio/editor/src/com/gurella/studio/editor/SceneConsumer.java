package com.gurella.studio.editor;

import com.gurella.engine.plugin.Plugin;
import com.gurella.engine.scene.Scene;

public interface SceneConsumer extends Plugin {
	void setScene(Scene scene);
}
