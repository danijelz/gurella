package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TapInfo;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeTapListener extends NodeEventSubscription {
	void onTap(TapInfo tapInfo);
}
