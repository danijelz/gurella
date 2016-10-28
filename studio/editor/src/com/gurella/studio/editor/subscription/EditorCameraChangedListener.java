package com.gurella.studio.editor.subscription;

import com.badlogic.gdx.graphics.Camera;
import com.gurella.engine.event.EventSubscription;

public interface EditorCameraChangedListener extends EventSubscription {
	void cameraChanged(Camera camera);
}
