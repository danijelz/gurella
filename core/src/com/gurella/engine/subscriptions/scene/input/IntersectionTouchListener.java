package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface IntersectionTouchListener extends EventSubscription {
	void onTouchDown(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);

	void onTouchUp(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);
}
