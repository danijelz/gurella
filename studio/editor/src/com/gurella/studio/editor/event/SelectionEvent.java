package com.gurella.studio.editor.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.subscription.EditorSelectionListener;

public class SelectionEvent implements Event<EditorSelectionListener> {
	private Object selection;

	public SelectionEvent(Object selection) {
		this.selection = selection;
	}

	@Override
	public Class<EditorSelectionListener> getSubscriptionType() {
		return EditorSelectionListener.class;
	}

	@Override
	public void dispatch(EditorSelectionListener subscriber) {
		subscriber.selectionChanged(selection);
	}
}
