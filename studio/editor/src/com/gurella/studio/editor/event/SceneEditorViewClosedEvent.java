package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.subscription.EditorViewClosedListener;

public class SceneEditorViewClosedEvent implements Event<EditorViewClosedListener> {
	private DockableView view;

	public SceneEditorViewClosedEvent(DockableView view) {
		this.view = view;
	}

	@Override
	public Class<EditorViewClosedListener> getSubscriptionType() {
		return EditorViewClosedListener.class;
	}

	@Override
	public void dispatch(EditorViewClosedListener subscriber) {
		subscriber.viewClosed(view);
	}
}
