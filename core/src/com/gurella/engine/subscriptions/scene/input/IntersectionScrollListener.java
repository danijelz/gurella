package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface IntersectionScrollListener extends EventSubscription {
	void onScrolled(RenderableComponent renderableComponent, int screenX, int screenY, int amount,
			Vector3 intersection);
}
