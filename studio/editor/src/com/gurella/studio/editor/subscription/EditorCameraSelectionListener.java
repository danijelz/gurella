package com.gurella.studio.editor.subscription;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.event.EventSubscription;

//TODO replace with plugins
public interface EditorCameraSelectionListener extends EventSubscription {
	void cameraChanged(Camera camera);
}
