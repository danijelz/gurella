package com.gurella.engine.subscriptions.application;

import com.badlogic.gdx.graphics.Camera;

public interface ApplicationDebugRenderListener extends ApplicationEventSubscription {
	void debugRender(Camera camera);
}
