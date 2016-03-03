package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTapListener extends NodeEventSubscription {
	void onTap(IntersectionTouchEvent touchEvent, int count);
}
