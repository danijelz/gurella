package com.gurella.engine.subscriptions.scene.renderable;

import com.gurella.engine.scene.renderable.RenderableComponent;
import com.gurella.engine.subscriptions.scene.SceneEventSubscription;

public interface SceneRenderableChangedListener extends SceneEventSubscription {
	void onRenderableChanged(RenderableComponent component);
}
