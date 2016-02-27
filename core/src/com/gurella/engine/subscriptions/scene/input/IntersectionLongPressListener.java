package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface IntersectionLongPressListener extends EventSubscription {
	void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);
}
