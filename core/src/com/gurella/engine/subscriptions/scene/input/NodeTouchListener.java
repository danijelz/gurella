package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTouchListener extends NodeEventSubscription {
	void onTouchDown(PointerInfo pointerInfo);

	void onTouchUp(PointerInfo pointerInfo);
}
