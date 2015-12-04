package com.gurella.engine.graph.ui;

import com.gurella.engine.graph.BaseSceneElementType;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.layer.Layer;

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
