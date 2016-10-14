package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.part.SceneEditorView;
import com.gurella.studio.editor.subscription.SceneEditorViewClosedListener;

public class SceneEditorViewClosedEvent implements Event<SceneEditorViewClosedListener> {
	private SceneEditorView view;

	public SceneEditorViewClosedEvent(SceneEditorView view) {
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
