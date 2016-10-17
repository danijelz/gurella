package com.gurella.engine.subscriptions.scene.transform;

import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTransformChangedListener extends NodeEventSubscription {
	void onNodeTransformChanged();
}
