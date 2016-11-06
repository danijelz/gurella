package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.MouseMoveInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneMouseListener extends SceneEventSubscription {
	void mouseMoved(MouseMoveInfo mouseMoveInfo);
}
