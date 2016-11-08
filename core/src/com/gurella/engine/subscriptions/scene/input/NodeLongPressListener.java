package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeLongPressListener extends NodeEventSubscription {
	void onLongPress(PointerInfo pointerInfo);
}
