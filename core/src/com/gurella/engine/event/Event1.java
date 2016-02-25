package com.gurella.engine.event;

public abstract class Event1<T> implements Event<Listener1<T>> {
	protected T value;

	public Event1() {
	}

	public Event1(T value) {
		this.value = value;
	}

	@Override
	public void notify(Listener1<T> listener) {
		listener.handle(value);
	}
}
