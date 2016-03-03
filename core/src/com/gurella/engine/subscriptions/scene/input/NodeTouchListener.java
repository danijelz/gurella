package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTouchListener extends NodeEventSubscription {
	void onTouchDown(IntersectionTouchEvent touchEvent);

	void onTouchUp(IntersectionTouchEvent touchEvent);
}