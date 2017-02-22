package com.gurella.engine.test;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.gurella.engine.scene.SceneNodeComponent;
import com.gurella.engine.scene.transform.TransformComponent;
import com.gurella.engine.subscriptions.scene.NodeComponentActivityListener;
import com.gurella.engine.subscriptions.scene.update.LogicUpdateListener;

public class SampleComponent extends SceneNodeComponent implements LogicUpdateListener, NodeComponentActivityListener {
	transient TransformComponent transformComponent;

	@Override
	protected void componentActivated() {
		transformComponent = getNode().getComponent(TransformComponent.class);
	}

	@Override
	protected void componentDeactivated() {
		transformComponent = null;
	}

	@Override
	public void onNodeComponentActivated(SceneNodeComponent component) {
		if (component instanceof TransformComponent) {
			this.transformComponent = (TransformComponent) component;
		}
	}

	@Override
	public void onNodeComponentDeactivated(SceneNodeComponent component) {
		if (component instanceof TransformComponent) {
			transformComponent = null;
		}
	}

	@Override
	public void onLogicUpdate() {
		if (transformComponent != null) {
			float sin = 0.05f * MathUtils.sinDeg(TimeUtils.millis() % 360);
			transformComponent.translateY(sin);
		}
	}
}
