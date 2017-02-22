package com.gurella.engine.scene.camera;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.camera.CameraOrdinalChangedListener;

class CameraOrdinalChangedEvent implements Event<CameraOrdinalChangedListener>, Poolable {
	CameraComponent<?> cameraComponent;

	@Override
	public Class<CameraOrdinalChangedListener> getSubscriptionType() {
		return CameraOrdinalChangedListener.class;
	}

	@Override
	public void dispatch(CameraOrdinalChangedListener subscriber) {
		subscriber.onOrdinalChanged(cameraComponent);
	}

	@Override
	public void reset() {
		cameraComponent = null;
	}
}
