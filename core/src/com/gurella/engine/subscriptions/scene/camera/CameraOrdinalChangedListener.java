package com.gurella.engine.subscriptions.scene.camera;

import com.gurella.engine.scene.camera.CameraComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface CameraOrdinalChangedListener extends SceneEventSubscription {
	void onOrdinalChanged(CameraComponent<?> cameraComponent);
}
