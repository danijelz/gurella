package com.gurella.studio.editor.scene.event;

import com.gurella.engine.event.Event;
import com.gurella.studio.editor.subscription.SelectionListener;

public class SelectionEvent implements Event<SelectionListener> {
	private Object selection;

	public SelectionEvent(Object selection) {
		this.selection = selection;
	}

	@Override
	public Class<SelectionListener> getSubscriptionType() {
		return SelectionListener.class;
	}

	@Override
	public void dispatch(SelectionListener subscriber) {
		subscriber.selectionChanged(selection);
	}
}
