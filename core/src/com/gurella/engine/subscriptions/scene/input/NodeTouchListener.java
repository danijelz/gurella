package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TouchInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTouchListener extends NodeEventSubscription {
	void onTouchDown(TouchInfo touchInfo);

	void onTouchUp(TouchInfo touchInfo);
}
