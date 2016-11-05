package com.gurella.studio.editor.camera;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.plugin.Plugin;

public interface CameraProviderExtension extends Plugin {
	void setCamera(Camera camera);
}
