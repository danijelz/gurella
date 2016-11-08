package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragListener extends NodeEventSubscription {
	void onDragStart(PointerInfo pointerInfo);

	void onDragMove(PointerInfo pointerInfo);

	void onDragEnd(PointerInfo pointerInfo);
}
