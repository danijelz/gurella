package com.gurella.engine.subscriptions.scene.update;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface PreRenderUpdateListener extends SceneEventSubscription {
	void onPreRenderUpdate();
}
