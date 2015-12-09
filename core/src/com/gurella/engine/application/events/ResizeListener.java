package com.gurella.engine.application.events;

import com.gurella.engine.graph.event.EventCallback;

public interface ResizeListener {
	@EventCallback
	void onResize(int width, int height);
}
