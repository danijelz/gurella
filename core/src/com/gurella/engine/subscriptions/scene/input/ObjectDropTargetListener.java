package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.DragSource;
import com.gurella.engine.scene.input.DropTarget;

public interface ObjectDropTargetListener extends EventSubscription {
	DropTarget getDropTarget(Array<DragSource> dragSources);
}
