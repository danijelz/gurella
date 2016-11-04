package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.subscription.SceneDirtyListener;

public class SceneChangedEvent implements Event<SceneDirtyListener> {
	public static final SceneChangedEvent instance = new SceneChangedEvent();

	private SceneChangedEvent() {
	}

	@Override
	public Class<SceneDirtyListener> getSubscriptionType() {
		return SceneDirtyListener.class;
	}

	@Override
	public void dispatch(SceneDirtyListener listener) {
		listener.sceneDirty();
	}
}
