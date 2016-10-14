package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.part.DockableView;
import com.gurella.studio.editor.subscription.SceneEditorViewClosedListener;

public class SceneEditorViewClosedEvent implements Event<SceneEditorViewClosedListener> {
	private DockableView view;

	public SceneEditorViewClosedEvent(DockableView view) {
		this.view = view;
	}

	@Override
	public Class<SceneEditorViewClosedListener> getSubscriptionType() {
		return SceneEditorViewClosedListener.class;
	}

	@Override
	public void dispatch(SceneEditorViewClosedListener subscriber) {
		subscriber.viewClosed(view);
	}
}
