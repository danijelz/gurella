package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.input.TouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface IntersectionDragOverListener extends SceneEventSubscription {
	void onDragOverStart(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);

	void onDragOverMove(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);

	void onDragOverEnd(RenderableComponent renderableComponent, TouchEvent touchEvent);
}
