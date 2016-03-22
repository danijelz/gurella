package com.gurella.engine.subscriptions.scene.update;

import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface PostRenderUpdateListener extends SceneEventSubscription {
	void onPostRenderUpdate();
}
