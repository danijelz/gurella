package com.gurella.engine.graph.ui;

import com.gurella.engine.graph.BaseSceneElementType;
import com.gurella.engine.graph.layer.Layer;
import com.gurella.engine.graph.script.ScriptComponent;

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
