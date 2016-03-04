package com.gurella.engine.subscriptions.scene.movement;

import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTransformChangedListener extends NodeEventSubscription {
	void onNodeTransformChanged();
}
