package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.DragSource;
import com.gurella.engine.scene.input.DragStartCondition;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragSourceListener extends NodeEventSubscription {
	DragSource getDragSource(DragStartCondition dragStartCondition);
}
