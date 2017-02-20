package com.gurella.engine.scene.ui;

import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent;

@BaseSceneElement
public abstract class UiComponent extends SceneNodeComponent {
	int x;
	int y;
	int w;
	int h;

	boolean visible;
	boolean uiEnabled;

	Composite parent;
	UiSystem uiSystem;

	public void requestFocus() {
		if (isFocusable()) {
			uiSystem.focusManager.requestFocus(this);
		}
	}

	public boolean hasFocus() {
		if (isFocusable()) {
			return uiSystem.focusManager.hasFocus(this);
		} else {
			return false;
		}
	}

	public boolean isFocusable() {
		return false;
	}

	public void setFocusable() {
	}

	void setFocused(boolean focused) {
		//TODO FocusInfo
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}
