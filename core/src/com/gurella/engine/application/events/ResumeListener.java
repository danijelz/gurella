package com.gurella.engine.application.events;

import com.gurella.engine.graph.event.EventCallback;

public interface ResumeListener {
	@EventCallback
	void onResume();
}
