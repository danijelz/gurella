package com.gurella.studio.editor;

import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.plugin.Plugin;

public interface SceneConsumer extends Plugin {
	void setScene(Scene scene);
}
