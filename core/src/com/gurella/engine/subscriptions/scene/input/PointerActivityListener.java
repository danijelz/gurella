package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.PointerTrack;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface PointerActivityListener extends SceneEventSubscription {
	void onPointerActivity(int pointer, int button, PointerTrack pointerTrack);
}