package com.gurella.engine.event;

import com.gurella.engine.signal.Listener0;

public abstract class Listener0Event implements Event<Listener0>{
	@Override
	public void notify(Listener0 listener) {
		listener.handle();
	}
}
