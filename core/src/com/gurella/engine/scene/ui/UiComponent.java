package com.gurella.engine.scene.ui;

import com.gurella.engine.scene.BaseSceneElement;
import com.gurella.engine.scene.SceneNodeComponent2;

@BaseSceneElement
public class UiComponent extends SceneNodeComponent2 {
	public int x;
	public int y;
	public int w;
	public int h;
	public boolean visible;
	public boolean blocked;
	
	Composite parent;

	// TODO
	public void requestFocus() {
	}

	public boolean hasFocus() {
		return false;
	}

	public boolean isFocusable() {
		return false;
	}

	public void setFocusable() {
	}
}
