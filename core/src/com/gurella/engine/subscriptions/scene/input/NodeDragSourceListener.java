package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.dnd.DragSource;
import com.gurella.engine.scene.input.dnd.DragStartCondition;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDragSourceListener extends NodeEventSubscription {
	DragSource getDragSource(DragStartCondition dragStartCondition);
}
