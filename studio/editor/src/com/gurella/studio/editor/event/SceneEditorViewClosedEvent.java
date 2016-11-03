package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.control.DockableView;
import com.gurella.studio.editor.subscription.EditorViewActivityListener;

public class SceneEditorViewClosedEvent implements Event<EditorViewActivityListener> {
	private DockableView view;

	public SceneEditorViewClosedEvent(DockableView view) {
		this.view = view;
	}

	@Override
	public Class<EditorViewActivityListener> getSubscriptionType() {
		return EditorViewActivityListener.class;
	}

	@Override
	public void dispatch(EditorViewActivityListener listener) {
		listener.viewClosed(view);
	}
}
