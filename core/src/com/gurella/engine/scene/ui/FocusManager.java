package com.gurella.engine.scene.ui;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.SceneKeyListener;
import com.gurella.engine.subscriptions.scene.input.SceneKeyTypedListener;

class FocusManager implements SceneKeyListener, SceneKeyTypedListener {
	private final Scene scene;
	private UiComponent focusedComponent;

	FocusManager(Scene scene) {
		this.scene = scene;
	}

	void activate() {
		EventService.subscribe(scene.getInstanceId(), this);
	}

	void deactivate() {
		EventService.unsubscribe(scene.getInstanceId(), this);
	}

	@Override
	public void keyDown(int keycode) {
		if (focusedComponent == null) {
			return;
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void keyUp(int keycode) {
		if (focusedComponent == null) {
			return;
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(char character) {
		if (focusedComponent == null) {
			return;
		}
		// TODO Auto-generated method stub
	}

	void requestFocus(UiComponent uiComponent) {
		if (uiComponent.isFocusable()) {
			if (focusedComponent != null) {
				focusedComponent.setFocused(false);
			}

			focusedComponent = uiComponent;
			focusedComponent.setFocused(true);
		}
	}

	public boolean hasFocus(UiComponent uiComponent) {
		return uiComponent == focusedComponent;
	}

	public void nextFocus() {
		// TODO
	}

	public void prewFocus() {
		// TODO
	}
}