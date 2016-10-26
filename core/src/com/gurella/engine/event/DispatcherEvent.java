package com.gurella.engine.event;

import com.badlogic.gdx.utils.Pool.Poolable;

public class DispatcherEvent<L extends EventSubscription> implements Event<L>, Poolable {
	Class<L> subscriptionType;
	Consumer<L> dispatcher;

	public DispatcherEvent() {
	}

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

	public interface Consumer<T> {
		void accept(T t);
	}

	@Override
	public void reset() {
		subscriptionType = null;
		dispatcher = null;
	}
}