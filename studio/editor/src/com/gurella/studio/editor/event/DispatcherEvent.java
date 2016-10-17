package com.gurella.studio.editor.event;

import java.util.function.Consumer;

import com.gurella.engine.event.Event;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;

public class DispatcherEvent<L extends EventSubscription> implements Event<L> {
	private Class<L> subscriptionType;
	private Consumer<L> dispatcher;

	public DispatcherEvent(Class<L> subscriptionType, Consumer<L> dispatcher) {
		this.subscriptionType = subscriptionType;
		this.dispatcher = dispatcher;
	}

	@Override
	public void dispatch(L listener) {
		dispatcher.accept(listener);
	}

	@Override
	public Class<L> getSubscriptionType() {
		return subscriptionType;
	}

	public static <L extends EventSubscription> void post(Class<L> type, Consumer<L> dispatcher) {
		EventService.post(new DispatcherEvent<L>(type, dispatcher));
	}

	public static <L extends EventSubscription> void post(int channel, Class<L> type, Consumer<L> dispatcher) {
		EventService.post(channel, new DispatcherEvent<L>(type, dispatcher));
	}
}