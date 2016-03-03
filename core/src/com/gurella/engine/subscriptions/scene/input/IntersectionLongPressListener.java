package com.gurella.engine.subscriptions.scene.input;

import com.gurella.engine.scene.input.IntersectionTouchEvent;
import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface IntersectionLongPressListener extends SceneEventSubscription {
	void onLongPress(RenderableComponent renderableComponent, IntersectionTouchEvent touchEvent);
}
