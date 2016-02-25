package com.gurella.engine.event;

public abstract class Event0 implements Event<Listener0>{
	@Override
	public void notify(Listener0 listener) {
		listener.handle();
	}
}
