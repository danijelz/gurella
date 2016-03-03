package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface IntersectionTouchListener extends SceneEventSubscription {
	void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);

	void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);
}
