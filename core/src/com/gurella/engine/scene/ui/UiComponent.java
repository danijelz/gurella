package com.gurella.engine.scene.ui;

import com.gurella.engine.scene.BaseSceneElementType;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.layer.Layer;

@BaseSceneElementType
public class UiComponent extends BehaviourComponent {
	public Layer layer;
	public int x;
	public int y;
	public int w;
	public int h;
	public boolean visible;
	public boolean blocked;
	
	//TODO
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
