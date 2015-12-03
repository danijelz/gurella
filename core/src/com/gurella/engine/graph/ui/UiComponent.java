package com.gurella.engine.graph.ui;

import com.gurella.engine.graph.BaseSceneElementType;
import com.gurella.engine.graph.behaviour.ScriptComponent;
import com.gurella.engine.graph.layer.Layer;

@BaseSceneElementType
public class UiComponent extends ScriptComponent {
	public Layer layer;
	private int x;
	private int y;
	private int w;
	private int h;
	private boolean visible;
	private boolean blocked;
}
