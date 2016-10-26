package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.subscription.EditorViewsListener;

public class SceneEditorViewClosedEvent implements Event<EditorViewsListener> {
	private DockableView view;

	public SceneEditorViewClosedEvent(DockableView view) {
		this.view = view;
	}

	@Override
	public Class<EditorViewsListener> getSubscriptionType() {
		return EditorViewsListener.class;
	}

	@Override
	public void dispatch(EditorViewsListener subscriber) {
		subscriber.viewClosed(view);
	}
}
