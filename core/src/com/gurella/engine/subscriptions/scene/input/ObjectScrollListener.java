package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventSubscription;

public interface ObjectScrollListener extends EventSubscription {
	void onScrolled(int screenX, int screenY, int amount, Vector3 intersection);
}
