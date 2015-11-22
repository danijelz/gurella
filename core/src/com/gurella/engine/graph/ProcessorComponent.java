package com.gurella.engine.graph;

import com.gurella.engine.application.UpdateListener;

public abstract class ProcessorComponent extends SceneNodeComponent implements UpdateListener {
	public ProcessorComponent() {
		SceneGraphUtils.asUpdateListener(this);
	}
}
