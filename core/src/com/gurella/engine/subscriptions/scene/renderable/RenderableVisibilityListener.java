package com.gurella.engine.subscriptions.scene.renderable;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface RenderableVisibilityListener extends SceneEventSubscription {
	void onVisibilityChanged(boolean visible);
}
