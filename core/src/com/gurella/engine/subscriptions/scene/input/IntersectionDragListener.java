package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;

public interface IntersectionDragListener extends EventSubscription {
	void onDragStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);

	void onDragMove(RenderableComponent renderableComponent, TouchEvent touchEvent);

	void onDragEnd(RenderableComponent renderableComponent, TouchEvent touchEvent);
}
