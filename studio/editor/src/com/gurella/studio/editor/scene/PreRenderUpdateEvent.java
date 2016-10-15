package com.gurella.studio.editor.scene;

import com.gurella.engine.event.Event;
import com.gurella.engine.subscriptions.scene.update.PreRenderUpdateListener;

class PreRenderUpdateEvent implements Event<PreRenderUpdateListener> {
	public static final PreRenderUpdateEvent instance = new PreRenderUpdateEvent();

	@Override
	public Class<PreRenderUpdateListener> getSubscriptionType() {
		return PreRenderUpdateListener.class;
	}

	@Override
	public void dispatch(PreRenderUpdateListener listener) {
		listener.onPreRenderUpdate();
	}
}