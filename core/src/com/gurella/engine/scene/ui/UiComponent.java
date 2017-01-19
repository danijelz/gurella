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
	
	void setFocused(boolean focused) {
		//TODO FocusInfo
	}
}
