package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDoubleTouchDownListener extends NodeEventSubscription {
	void onDoubleTouch(IntersectionTouchEvent touchEvent);
}
