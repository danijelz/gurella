package com.gurella.engine.scene.ui;

import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent;

@BaseSceneElement
public class UiComponent extends SceneNodeComponent {
	public int x;
	public int y;
	public int w;
	public int h;
	public boolean visible;
	public boolean blocked;
	
	CompositeComponent parent;
	UiSystem uiSystem;

	// TODO
	public void requestFocus() {
		if(isFocusable()) {
			uiSystem.uiFocusManager.requestFocus(this);
		}
	}

	public boolean hasFocus() {
		if(isFocusable()) {
			return uiSystem.uiFocusManager.hasFocus(this);
		} else {
			return false;
		}
	}

	public boolean isFocusable() {
		return false;
	}

	public void setFocusable() {
	}
}
