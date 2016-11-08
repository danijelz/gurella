package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragOverListener extends NodeEventSubscription {
	void onDragOverStart(PointerInfo pointerInfo);

	void onDragOverMove(PointerInfo pointerInfo);

	void onDragOverEnd(PointerInfo pointerInfo);
}
