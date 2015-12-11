package com.gurella.engine.scene.ui;

import com.gurella.engine.scene.BaseSceneElementType;
import com.gurella.engine.scene.behaviour.BehaviourComponent;
import com.gurella.engine.scene.layer.Layer;

@BaseSceneElementType
public class UiComponent extends BehaviourComponent {
	public Layer layer;
	private int x;
	private int y;
	private int w;
	private int h;
	private boolean visible;
	private boolean blocked;
}
