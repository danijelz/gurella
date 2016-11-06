package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.utils.Array;
import com.gurella.engine.scene.input.dnd.DragSource;
import com.gurella.engine.scene.input.dnd.DropTarget;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeDropTargetListener extends NodeEventSubscription {
	DropTarget getDropTarget(Array<DragSource> dragSources);
}
