package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.TapInfo;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneTapListener extends SceneEventSubscription {
	void onTap(TapInfo tapInfo);
}
