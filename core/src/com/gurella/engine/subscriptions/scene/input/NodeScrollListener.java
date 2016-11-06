package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.ScrollInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeScrollListener extends NodeEventSubscription {
	void onScrolled(ScrollInfo scrollInfo);
}
