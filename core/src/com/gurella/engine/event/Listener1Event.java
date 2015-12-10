package com.gurella.engine.event;

public abstract class Listener1Event<T> implements Event<Listener1<T>> {
	protected T value;

	public Listener1Event() {
	}

	public Listener1Event(T value) {
		this.value = value;
	}

	@Override
	public void notify(Listener1<T> listener) {
		listener.handle(value);
	}
}
