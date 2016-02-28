package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.DragSource;
import com.gurella.engine.scene.input.DragStartCondition;

public interface ObjectDragSourceListener extends EventSubscription {
	DragSource getDragSource(DragStartCondition dragStartCondition);
}
