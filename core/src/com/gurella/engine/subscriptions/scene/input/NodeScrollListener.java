package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.subscriptions.scene.NodeEventSubscription;

public interface NodeScrollListener extends NodeEventSubscription {
	void onScrolled(int screenX, int screenY, int amount, Vector3 intersection);
}
