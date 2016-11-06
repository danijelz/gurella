package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.MouseMoveInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeMouseOverListener extends NodeEventSubscription {
	void onMouseOverStart(MouseMoveInfo mouseMoveInfo);

	void onMouseOverMove(MouseMoveInfo mouseMoveInfo);

	void onMouseOverEnd(MouseMoveInfo mouseMoveInfo);
}
