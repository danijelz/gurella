package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface IntersectionMouseListener extends EventSubscription {
	void onMouseOverStart(RenderableComponent renderableComponent, int screenX, int screenY, Vector3 intersection);

	void onMouseOverMove(RenderableComponent renderableComponent, int screenX, int screenY, Vector3 intersection);

	void onMouseOverEnd(RenderableComponent renderableComponent, int screenX, int screenY);
}
