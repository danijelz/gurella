package com.gurella.studio.editor.subscription;

import com.gurella.engine.event.EventSubscription;

public interface EditorCameraSwitch extends EventSubscription {
	void switchCamera(CameraType cameraType);

	public enum CameraType {
		camera2d, camera3d;
	}
}
