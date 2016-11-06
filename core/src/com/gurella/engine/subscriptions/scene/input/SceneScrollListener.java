package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.ScrollInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneScrollListener extends SceneEventSubscription {
	void onScrolled(ScrollInfo scrollInfo);
}
