package com.gurella.engine.scene.ui;

import com.gurella.engine.event.EventService;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.subscriptions.scene.input.SceneKeyListener;
import com.gurella.engine.subscriptions.scene.input.SceneKeyTypedListener;

public class UiFocusManager implements SceneKeyListener, SceneKeyTypedListener {
	private final Scene scene;
	private UiComponent focusedComponent;

	public UiFocusManager(Scene scene) {
		this.scene = scene;
	}

	public void activate() {
		EventService.subscribe(scene.getInstanceId(), this);
	}

	public void deactivate() {
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

	public void requestFocus(UiComponent uiComponent) {
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