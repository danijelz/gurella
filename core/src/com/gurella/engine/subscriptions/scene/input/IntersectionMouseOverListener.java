package com.gurella.engine.subscriptions.scene.input;

import com.badlogic.gdx.math.Vector3;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface IntersectionMouseOverListener extends SceneEventSubscription {
	void onMouseOverStart(RenderableComponent intersected, int screenX, int screenY, Vector3 intersection);

	void onMouseOverMove(RenderableComponent intersected, int screenX, int screenY, Vector3 intersection);

	void onMouseOverEnd(RenderableComponent intersected, int screenX, int screenY);
}
