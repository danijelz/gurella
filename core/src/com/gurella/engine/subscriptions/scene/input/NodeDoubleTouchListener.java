package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.DoubleTouchInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDoubleTouchListener extends NodeEventSubscription {
	void onDoubleTouch(DoubleTouchInfo doubleTouchInfo);
}
