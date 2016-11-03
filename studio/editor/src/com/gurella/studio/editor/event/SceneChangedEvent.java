package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.subscription.SceneChangedListener;

public class SceneChangedEvent implements Event<SceneChangedListener> {
	public static final SceneChangedEvent instance = new SceneChangedEvent();

	private SceneChangedEvent() {
	}

	@Override
	public Class<SceneChangedListener> getSubscriptionType() {
		return SceneChangedListener.class;
	}

	@Override
	public void dispatch(SceneChangedListener listener) {
		listener.sceneChanged();
	}
}
