package com.gurella.engine.scene.ui;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.scene.input.MouseMoveInfo;
import com.gurella.engine.scene.input.ScrollInfo;
import com.gurella.engine.subscriptions.scene.input.SceneMouseListener;
import com.gurella.engine.subscriptions.scene.input.SceneScrollListener;

public class ScrollManager implements SceneMouseListener, SceneScrollListener {
	private final Scene scene;
	private Scrollable focusedComponent;

	public ScrollManager(Scene scene) {
		this.scene = scene;
	}

	public void activate() {
		EventService.subscribe(scene.getInstanceId(), this);
	}

	public void deactivate() {
		EventService.unsubscribe(scene.getInstanceId(), this);
	}

	@Override
	public void mouseMoved(MouseMoveInfo mouseMoveInfo) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScrolled(ScrollInfo scrollInfo) {
		// TODO Auto-generated method stub
	}
}
